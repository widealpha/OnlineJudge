package cn.sdu.oj.service;

import cn.sdu.oj.dao.*;
import cn.sdu.oj.domain.bo.JudgeLimit;
import cn.sdu.oj.domain.bo.JudgeResult;
import cn.sdu.oj.domain.bo.JudgeStatus;
import cn.sdu.oj.domain.bo.JudgeTask;
import cn.sdu.oj.domain.dto.SolveResultDto;
import cn.sdu.oj.domain.po.*;
import cn.sdu.oj.domain.vo.ProblemTypeEnum;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;


@Service
public class SolveService {
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
    ProblemSetService problemSetService;
    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    @Transactional
    public ResultEntity<String> trySolveProblem(JudgeTask task, int userId, int problemSetId) {
        Integer typeProblemId = generalProblemMapper.selectTypeProblemIdById(task.getProblemId());
        if (typeProblemId == null) {
            return ResultEntity.error(StatusCode.PROBLEM_NOT_EXIST);

        }
        AsyncProblem problem = asyncProblemMapper.selectProblem(typeProblemId);
        //todo 判断用户能否提交
        if (problem == null) {
            return ResultEntity.error(StatusCode.PROBLEM_NOT_EXIST);
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
            if (solveRecordMapper.unfinishedSolveCount(userId) >= 3) {
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
            rabbitTemplate.convertAndSend(routingKey, JSONObject.toJSONString(task));
            return ResultEntity.data(task.getTaskId());
        } catch (Exception e) {
            //可能是在数据库插入失败
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }

    }

    @Transactional
    public ResultEntity<AnswerRecord> trySolveSyncProblem(int problemId, int userId, int problemSetId, String userAnswer) {
        //todo 判断用户能否提交
        boolean can_submit = problemSetService.getUserCanTrySolveProblemSet(userId,problemSetId);


        AnswerRecord answerRecord = new AnswerRecord();
        answerRecord.setProblemId(problemId);
        answerRecord.setUserId(userId);
        answerRecord.setProblemSetId(problemSetId);
        //判断之前是否判过此题，如果有返回上一次判题的结果
        Integer recordId = answerRecordMapper.exist(answerRecord);
        if (recordId != null) {
            answerRecord.setId(recordId);
            return ResultEntity.data(answerRecordMapper.selectAnswerRecord(recordId));
        }

        GeneralProblem generalProblem = generalProblemMapper.selectGeneralProblem(problemId);
        if (generalProblem == null) {
            return ResultEntity.error(StatusCode.DATA_NOT_EXIST);
        }
        SyncProblem syncProblem = syncProblemMapper.selectProblem(generalProblem.getTypeProblemId());

        answerRecord.setUserAnswer(userAnswer);
        answerRecord.setType(syncProblem.getType());
        //是选择题的情况
        if (answerRecord.getType() == ProblemTypeEnum.SELECTION.id) {
            //单选多选一致
            HashSet<String> userAnswerSet = new HashSet<>(JSON.parseArray(userAnswer, String.class));
            HashSet<String> answerSet = new HashSet<>(JSON.parseArray(syncProblem.getAnswer(), String.class));
            answerRecord.setCorrect(userAnswerSet.equals(answerSet));
        } else if (answerRecord.getType() == ProblemTypeEnum.COMPLETION.id) {
            //填空题
            List<String> userAnswerList = JSON.parseArray(userAnswer, String.class);
            List<String> answerList = JSON.parseArray(syncProblem.getAnswer(), String.class);
            answerRecord.setCorrect(userAnswerList.equals(answerList));
        } else if (answerRecord.getType() == ProblemTypeEnum.JUDGEMENT.id) {
            //判断题
            HashSet<String> userAnswerSet = new HashSet<>(JSON.parseArray(userAnswer, String.class));
            HashSet<String> answerSet = new HashSet<>(JSON.parseArray(syncProblem.getAnswer(), String.class));
            answerRecord.setCorrect(userAnswerSet.equals(answerSet));
        } else if (answerRecord.getType() == ProblemTypeEnum.SHORT.id) {
            answerRecord.setCorrect(null);
        } else {
            return ResultEntity.error(StatusCode.COMMON_FAIL, "题目类型错误");
        }
        answerRecord.setCorrect(Objects.equals(userAnswer, syncProblem.getAnswer()));
        if (answerRecordMapper.insertAnswerRecord(answerRecord)) {
            return ResultEntity.data(answerRecord);
        } else {
            return ResultEntity.data(StatusCode.DATA_ALREADY_EXIST, null);
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
        //todo 判题结束，更新答卷中题目状态为完成
        //判题成功,插入判题的数据
        if (resultEntity.getCode() == JudgeStatus.JUDGE_SUCCESS.getCode()) {
            record.setStatus(JudgeStatus.JUDGE_SUCCESS.getCode());
            int allCpuTime = 0;
            int allMemory = 0;
            int allRealTime = 0;
            for (int i = 0; i < judgeResult.getCheckPointSize(); i++) {
                JudgeLimit detail = judgeResult.getDetails().get(i);
                if (detail != null) {
                    allCpuTime += detail.getCpuTime();
                    allMemory += detail.getMemory();
                    allRealTime += detail.getRealTime();
                }

            }
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
            //-1为规定的编译错误的信息
            record.setError(judgeResult.getErrors().get(-1));
        } else { // 其他错误对客户隐藏具体错误
            record.setError("评测异常");
        }
        solveRecordMapper.updateSolveRecordByPrimaryKey(record);
        return true;
    }
}
