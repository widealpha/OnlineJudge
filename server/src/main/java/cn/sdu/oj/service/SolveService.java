package cn.sdu.oj.service;

import cn.sdu.oj.dao.*;
import cn.sdu.oj.domain.bo.*;
import cn.sdu.oj.domain.dto.MinorUserInfoDto;
import cn.sdu.oj.domain.dto.ShortQuestionAnswerDto;
import cn.sdu.oj.domain.dto.SolveResultDto;
import cn.sdu.oj.domain.po.*;
import cn.sdu.oj.domain.vo.ProblemTypeEnum;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;


@Service
public class SolveService {
    private static final int SAME_TIME_SOLVE_LIMIT = 10;
    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    SolveRecordMapper solveRecordMapper;

    @Resource
    GeneralProblemMapper generalProblemMapper;

    @Resource
    AsyncProblemMapper asyncProblemMapper;
    @Resource
    SyncProblemMapper syncProblemMapper;
    @Resource
    AnswerRecordMapper answerRecordMapper;

    @Resource
    ProblemSetProblemMapper problemSetProblemMapper;

    @Resource
    UserInfoService userInfoService;

    @Resource
    ProblemSetService problemSetService;
    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    @Transactional
    public ResultEntity<String> trySolveProblem(JudgeTask task, int userId, int problemSetId) {
        //查看是否可以判题
        ResultEntity<Boolean> result = problemSetService.getUserCanTrySolveProblem(userId, task.getProblemId(), problemSetId);
        if (!result.getData()) {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY, result.getMessage());
        }
        Integer typeProblemId = generalProblemMapper.selectTypeProblemIdById(task.getProblemId());
        if (typeProblemId == null) {
            return ResultEntity.error(StatusCode.PROBLEM_NOT_EXIST);
        }
        AsyncProblem problem = asyncProblemMapper.selectProblem(typeProblemId);

        if (problem == null) {
            return ResultEntity.error(StatusCode.PROBLEM_NOT_EXIST);
        }
        if (!problem.getSupportLanguages().isEmpty() && !problem.getSupportLanguages().contains(task.getLanguage().name())) {
            return ResultEntity.error(StatusCode.LANGUAGE_NOT_SUPPORT);
        }
        //生成判题限制
        JudgeLimit judgeLimit = new JudgeLimit();
        ProblemLimit problemLimit = new ProblemLimit(problem);
        if (problemLimit.getCodeLength() != null && task.getCode().length() > problemLimit.getCodeLength()) {
            return ResultEntity.error(StatusCode.CODE_TOO_LONG);
        }
        judgeLimit.setCpuTime(problemLimit.getTime());
        //设置真实时间为cpu时间的两倍，防止调度问题
        judgeLimit.setRealTime(problemLimit.getTime() * 2);
        judgeLimit.setMemory(problemLimit.getMemory());
        try {
            //如果还没有判完的题目超出判题限制，拒绝判题
            if (solveRecordMapper.unfinishedSolveCount(userId) >= SAME_TIME_SOLVE_LIMIT) {
                return ResultEntity.data(StatusCode.OVER_SOLVE_LIMIT, null);
            }
            SolveRecord solveRecord = new SolveRecord();
            solveRecord.setProblemId(task.getProblemId());
            solveRecord.setProblemSetId(problemSetId);
            solveRecord.setUserId(userId);
            solveRecord.setCode(task.getCode());
            solveRecord.setLanguage(task.getLanguage().name());
            solveRecordMapper.insertRecord(solveRecord);
            //获取插入数据库返回的Id
            int recordId = solveRecord.getId();
            //插入成功后，将插入的数据的id作为taskId发送到mq
            task.setTaskId("" + recordId);
            task.setLimit(judgeLimit);
            rabbitTemplate.convertAndSend(routingKey, JSONObject.toJSONString(task, SerializerFeature.WriteEnumUsingToString));
            return ResultEntity.data(task.getTaskId());
        } catch (Exception e) {
            //可能是在数据库插入失败
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }

    }

    @Transactional
    public ResultEntity<AnswerRecord> trySolveSyncProblem(int problemId, int userId, int problemSetId, String userAnswer) {
        //查看是否可以判题
        ResultEntity<Boolean> result = problemSetService.getUserCanTrySolveProblem(userId, problemId, problemSetId);
        if (!result.getData()) {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY, result.getMessage());
        }
        AnswerRecord answerRecord = new AnswerRecord();
        answerRecord.setProblemId(problemId);
        answerRecord.setUserId(userId);
        answerRecord.setProblemSetId(problemSetId);
        //判断之前是否判过此题，如果有返回上一次判题的结果
        Integer recordId = answerRecordMapper.selectRecordByRelationIds(answerRecord);
        if (recordId != null) {
            answerRecord.setId(recordId);
            return ResultEntity.data(answerRecordMapper.selectAnswerRecord(recordId));
        }
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        } else if (generalProblem.getType() == ProblemTypeEnum.PROGRAMING.id) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST, "不可提交编程题");
        }
        SyncProblem syncProblem = syncProblemMapper.selectProblem(generalProblem.getTypeProblemId());

        answerRecord.setUserAnswer(userAnswer);
        answerRecord.setType(syncProblem.getType());
        Integer totalScore = problemSetProblemMapper.selectScoreByProblemSetAndProblemId(problemSetId, problemId);
        if (totalScore == null) {
            totalScore = 0;
        }
        if (!judge(answerRecord, syncProblem.getAnswer(), totalScore)) {
            return ResultEntity.error(StatusCode.COMMON_FAIL, "不支持此次判题");
        }
        if (answerRecordMapper.insertAnswerRecord(answerRecord)) {
            return ResultEntity.data(answerRecord);
        } else {
            return ResultEntity.data(StatusCode.DATA_ALREADY_EXIST, null);
        }
    }

    /**
     * 同步判题
     *
     * @param answerRecord 用户提交记录
     * @param standAnswer  标准答案
     * @param totalScore   题目总分数
     * @return 是否支持判题
     */
    private boolean judge(AnswerRecord answerRecord, String standAnswer, int totalScore) {
        int problemType = answerRecord.getType();
        try {
            if (problemType == ProblemTypeEnum.SELECTION.id || problemType == ProblemTypeEnum.JUDGEMENT.id) {
                if (standAnswer.equals(answerRecord.getUserAnswer())) {
                    answerRecord.setCorrect(true);
                    answerRecord.setScore(totalScore);
                } else {
                    answerRecord.setScore(0);
                }
                return true;
            } else if (problemType == ProblemTypeEnum.MULTIPLE_SELECTION.id) {
                int count = 0;
                HashSet<String> standAns = new HashSet<>(JSON.parseArray(standAnswer, String.class));
                for (String userAns : JSON.parseArray(answerRecord.getUserAnswer(), String.class)) {
                    if (standAns.contains(userAns)) {
                        count++;
                    } else {
                        //任何一个用户作答的答案不再其中判为错误
                        answerRecord.setCorrect(false);
                        answerRecord.setScore(0);
                        return true;
                    }
                }
                //没有少选判定正确
                if (count == standAns.size()) {
                    answerRecord.setCorrect(true);
                    answerRecord.setScore(totalScore);
                } else {
                    //少选得1/2向下取整的分数
                    answerRecord.setCorrect(false);
                    answerRecord.setScore(totalScore / 2);
                }
                return true;
            } else if (problemType == ProblemTypeEnum.COMPLETION.id) {
                List<String> standAnswerList = JSON.parseArray(standAnswer, String.class);
                List<String> userAnswerList = JSON.parseArray(answerRecord.getUserAnswer(), String.class);
                int count = 0;
                if (standAnswerList.size() != userAnswerList.size()) {
                    answerRecord.setCorrect(false);
                    answerRecord.setScore(0);
                    return true;
                }
                for (int i = 0; i < standAnswerList.size(); i++) {
                    if (standAnswerList.get(i).equals(userAnswerList.get(i))) {
                        count++;
                    }
                }
                //按照正确填空的数量定分
                if (count == standAnswerList.size()) {
                    answerRecord.setCorrect(true);
                    answerRecord.setScore(totalScore);
                } else {
                    answerRecord.setCorrect(false);
                    answerRecord.setScore(totalScore * count / standAnswerList.size());
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            //如果错误是格式有问题,视为尝试攻击系统,做判错处理
            answerRecord.setCorrect(false);
            answerRecord.setScore(0);
            return false;
        }

    }

    public ResultEntity<String> runTestCode(JudgeTask task, int userId, int problemSetId) {
        return trySolveProblem(task, userId, problemSetId);
    }

    public ResultEntity<SolveResultDto> solveResult(int taskId, int userId) {
        SolveRecord record = solveRecordMapper.selectSolveRecordByPrimaryKey(taskId);
        if (record == null || record.getUserId() != userId) {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY);
        }
        if (record.getStatus() == JudgeStatus.WAIT_JUDGE.getCode()) {
            return ResultEntity.data(StatusCode.WAIT_JUDGE, null);
        } else if (record.getStatus() == JudgeStatus.JUDGE_SUCCESS.getCode()) {
            //判题结果成功或者仅有测试用例不通过,直接返回测试结果
            return ResultEntity.data(StatusCode.SUCCESS, SolveResultDto.fromSolveRecord(record));
        } else if (record.getStatus() == JudgeStatus.JUDGE_WRONG_ANSWER.getCode()) {
            return ResultEntity.data(StatusCode.JUDGE_WRONG_ANSWER, SolveResultDto.fromSolveRecord(record));
        } else if (record.getStatus() == JudgeStatus.JUDGE_COMPILE_ERROR.getCode()) {
            return ResultEntity.data(StatusCode.JUDGE_COMPILE_ERROR, SolveResultDto.fromSolveRecord(record));
        } else if (record.getStatus() == JudgeStatus.JUDGE_TIME_OUT.getCode()) {
            return ResultEntity.data(StatusCode.JUDGE_TIME_OUT, null);
        } else if (record.getStatus() == JudgeStatus.JUDGE_MEMORY_OUT.getCode()) {
            return ResultEntity.data(StatusCode.JUDGE_MEMORY_OUT, null);
        } else if (record.getStatus() == JudgeStatus.JUDGE_RUNTIME_ERROR.getCode()) {
            return ResultEntity.data(StatusCode.JUDGE_RUNTIME_ERROR, SolveResultDto.fromSolveRecord(record));
        } else if (record.getStatus() == JudgeStatus.JUDGE_OUTPUT_OUT.getCode()) {
            return ResultEntity.data(StatusCode.JUDGE_OUTPUT_OUT, null);
        } else {
            return ResultEntity.data(StatusCode.JUDGE_SYSTEM_ERROR, null);
        }
    }

    public boolean solveResultReceive(ResultEntity<String> resultEntity) {
        JudgeResult judgeResult = JSONObject.parseObject(resultEntity.getData(), JudgeResult.class);
        SolveRecord record = solveRecordMapper.selectSolveRecordByPrimaryKey(Integer.parseInt(judgeResult.getTaskId()));
        record.setStatus(resultEntity.getCode());
        record.setCheckpointSize(judgeResult.getCheckPointSize());
        record.setTotalCorrect(0);

        //判题成功,插入判题的数据
        if (resultEntity.getCode() == JudgeStatus.JUDGE_SUCCESS.getCode()) {
            record.setStatus(JudgeStatus.JUDGE_SUCCESS.getCode());
            int allCpuTime = 0;
            int allMemory = 0;
            int allRealTime = 0;
            for (int i = 1; i <= judgeResult.getCheckPointSize(); i++) {
                JudgeLimit detail = judgeResult.getDetails().get("" + i);
                if (detail != null) {
                    allCpuTime += detail.getCpuTime();
                    allMemory += detail.getMemory();
                    allRealTime += detail.getRealTime();
                }

            }
            record.setTotalCorrect(judgeResult.getCheckPointSize());
            record.setCpuTime(allCpuTime / judgeResult.getCheckPointSize());
            record.setMemory(allMemory / judgeResult.getCheckPointSize());
            record.setRealTime(allRealTime / judgeResult.getCheckPointSize());
        } else if (resultEntity.getCode() == JudgeStatus.JUDGE_RUNTIME_ERROR.getCode() ||
                resultEntity.getCode() == JudgeStatus.JUDGE_TIME_OUT.getCode() ||
                resultEntity.getCode() == JudgeStatus.JUDGE_OUTPUT_OUT.getCode() ||
                resultEntity.getCode() == JudgeStatus.JUDGE_MEMORY_OUT.getCode() ||
                resultEntity.getCode() == JudgeStatus.JUDGE_WRONG_ANSWER.getCode()
        ) { // 运行时错误,超时,输出错误,内存错误将错误点的错误信息放入error字段
            record.setError(JSONObject.toJSONString(judgeResult.getErrors()));
//            for (int i = 0; i < judgeResult.getCheckPointSize(); i++) {
//                if (judgeResult.getErrors().containsKey(i)) {
//                    record.setError(judgeResult.getErrors().get(i));
//                    break;
//                }
//            }
        } else if (resultEntity.getCode() == JudgeStatus.JUDGE_COMPILE_ERROR.getCode()) { // 编译错误将错误信息放入error字段
            JSONObject object = new JSONObject();
            object.put("0", judgeResult.getErrors().get("0"));
            //0为规定的编译错误的信息
            record.setError(object.toJSONString());
        } else { // 其他错误对客户隐藏具体错误
            record.setError("{\"0\":\"评测异常\"}");
        }
        solveRecordMapper.updateSolveRecordByPrimaryKey(record);
        return true;
    }

    public ResultEntity<List<ShortQuestionAnswerDto>> shortQuestionAnswers(int userId, int problemId, int problemSetId) {
        ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);
        if (problemSet == null || problemSet.getCreatorId() != userId) {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY);
        }
        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        }
        SyncProblem syncProblem = syncProblemMapper.selectProblem(generalProblem.getTypeProblemId());
        if (syncProblem == null || syncProblem.getType() != ProblemTypeEnum.SHORT.id) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        }
        List<AnswerRecord> answers = answerRecordMapper.selectRecordByTypeAndRelationIds(
                ProblemTypeEnum.SHORT.id,
                problemId,
                problemSetId
        );

        List<ShortQuestionAnswerDto> userAnswers = new ArrayList<>();
        for (AnswerRecord answer : answers) {
            ShortQuestionAnswerDto answerDto = new ShortQuestionAnswerDto();
            answerDto.setRecordId(answer.getId());
            answerDto.setAnswer(answer.getUserAnswer());
            answerDto.setUserId(answer.getUserId());
            MinorUserInfoDto userInfo = userInfoService.minorUserInfo(answer.getUserId()).getData();
            answerDto.setUsername(userInfo.getUsername());
            answerDto.setAvatar(userInfo.getAvatar());
            answerDto.setProblemId(problemId);
            answerDto.setSubmitTime(answer.getCreateTime().toLocalDateTime());
            userAnswers.add(answerDto);
        }
        return ResultEntity.data(userAnswers);
    }

    public ResultEntity<Boolean> gradeQuestionAnswerRecord(int creator, int problemId, int problemSetId, int recordId, int score) {
        AnswerRecord answerRecord = answerRecordMapper.selectAnswerRecord(recordId);
        if (answerRecord.getProblemId() != problemId || answerRecord.getProblemSetId() != problemSetId) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        }
        ProblemSet problemSet = problemSetService.getProblemSetInfo(problemSetId);
        if (problemSet == null || problemSet.getCreatorId() != creator) {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY);
        }
        answerRecord.setScore(score);
        if (answerRecordMapper.updateScoreByPrimaryKey(answerRecord.getId(), score)) {
            return ResultEntity.data(true);
        } else {
            return ResultEntity.error(StatusCode.NO_PERMISSION_OR_EMPTY);
        }
    }

    public ResultEntity<SolveRecord> latestProblemCommitCode(int problemId, int problemSetId, Integer userId) {
        SolveRecord solveRecord = solveRecordMapper.latestRecord(problemId, problemSetId, userId);
        return ResultEntity.data(solveRecord);
    }
}
