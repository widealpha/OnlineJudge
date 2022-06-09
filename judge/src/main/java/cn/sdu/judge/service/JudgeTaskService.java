package cn.sdu.judge.service;

import cn.sdu.judge.bean.*;
import cn.sdu.judge.entity.ResultEntity;
import cn.sdu.judge.entity.StatusCode;
import cn.sdu.judge.exceptions.CurrentNotSupportException;
import cn.sdu.judge.judger.*;
import cn.sdu.judge.mapper.TaskRecordMapper;
import cn.sdu.judge.util.SftpFileUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class JudgeTaskService {
    @Resource
    TaskRecordMapper taskRecordMapper;
    Logger logger = LoggerFactory.getLogger(JudgeTaskService.class);
    @Resource
    SftpFileUtil sftpFileUtil;

    public ResultEntity judgeProblem(JudgeTask task) {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setTaskId(task.getTaskId());
        judgeResult.setProblemId(task.getProblemId());
        judgeResult.setCheckpointSize(0);

        if (taskRecordMapper.existTaskRecord(task.getTaskId())) {
            return ResultEntity.data(StatusCode.SAME_TASK_EXIST, judgeResult);
        }
        //新建task并插入数据库
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.setTaskId(task.getTaskId());
        taskRecord.setLanguage(task.getLanguage().name());
        taskRecord.setCode(task.getCode());
        taskRecord.setCodeLength(task.getCode().length());
        taskRecord.setSpecialJudge(task.isSpecialJudge());
        if (task.isSpecialJudge()) {
            taskRecord.setSpecialJudgeLanguage(task.getSpecialJudgeLanguage().name());
        }
        taskRecordMapper.insertTaskRecord(taskRecord);

        JudgeInterface judge = null;
        try {
            List<Checkpoint> checkpointList = sftpFileUtil.checkpoints(task.getProblemId());
            if (checkpointList == null || checkpointList.isEmpty()) {
                logger.warn("判题机错误或远程数据不存在");
                StatusCode status = StatusCode.PROBLEM_NOT_EXIST;
                taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), status.getCode());
                return ResultEntity.data(status, judgeResult);
            }
            //获取判题点的数量并填充到判题结果中
            judgeResult.setCheckpointSize(checkpointList.size());
            judge = fetchJudgeInterface(task.getLanguage());
            CompileInfo compileInfo = judge.compile(task.getCode());
            taskRecord.setCompileInfo(JSON.toJSONString(compileInfo));
            if (compileInfo.isSuccess()) {
                for (int i = 1; i <= checkpointList.size(); i++) {
                    Checkpoint checkpoint = checkpointList.get(i - 1);
                    RunInfo runInfo;
                    if (task.isTestMode()) {
                        runInfo = judge.run(task.getTestInput(), task.getLimit());
                        judgeResult.setOutput(runInfo.getOutput());
                    } else {
                        runInfo = judge.run(checkpoint, task.getLimit());
                    }
                    if (!runInfo.isSuccess()) {
                        StatusCode statusCode = mapStatusCode(runInfo.getSignal());
                        //如果评测机器没有报错但是测试点未通过
                        if (statusCode == StatusCode.JUDGE_SUCCESS) {
                            statusCode = StatusCode.JUDGE_WRONG_ANSWER;
                            judgeResult.getErrors().put(i, "测试点未通过");
                        } else {
                            judgeResult.getErrors().put(i, runInfo.getError());
                        }
                        runInfo.setCheckpoint(checkpoint);
                        taskRecord.setRunInfo(JSON.toJSONString(runInfo));
                        taskRecordMapper.updateTaskRecord(taskRecord);
                        taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), statusCode.getCode());
                        return ResultEntity.data(statusCode, judgeResult);
                    }
                    //填充判题结果
                    JudgeLimit detail = new JudgeLimit();
                    detail.setMemory(runInfo.getMemory());
                    detail.setCpuTime(runInfo.getCpuTime());
                    detail.setRealTime(runInfo.getRealTime());
                    judgeResult.getDetails().put(i, detail);
                }
            } else {
                StatusCode status = StatusCode.JUDGE_COMPILE_ERROR;
                taskRecordMapper.updateTaskRecord(taskRecord);
                taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), status.getCode());
                judgeResult.getErrors().put(0, compileInfo.getError());
                return ResultEntity.data(status, judgeResult);
            }
        } catch (Exception e) {
            logger.error("判题机错误或远程数据不存在: ", e);
            StatusCode status = StatusCode.PROBLEM_NOT_EXIST;
            taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), status.getCode());
            judgeResult.getErrors().put(0, e.getMessage());
            return ResultEntity.data(status, judgeResult);
        } finally {
            if (judge != null) {
                judge.clean();
            }
        }
        taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), StatusCode.JUDGE_SUCCESS.getCode());
        taskRecord.setRunInfo(JSON.toJSONString(judgeResult.getDetails()));
        taskRecordMapper.updateTaskRecord(taskRecord);
        return ResultEntity.data(StatusCode.JUDGE_SUCCESS, judgeResult);
    }

    JudgeInterface fetchJudgeInterface(LanguageEnum language) throws CurrentNotSupportException, IOException {
        switch (language) {
            case CPP17:
                return new CPPJudgeImpl();
            case JAVA8:
                return new JavaJudgeImpl();
            case C99:
                return new CJudgeImpl();
            case PYTHON3:
                return new PythonJudgeImpl();
            default:
                throw new CurrentNotSupportException(language);
        }
    }

    /**
     * SUCCESS = 0,
     * CPU_TIME_LIMIT_EXCEEDED = 1,
     * REAL_TIME_LIMIT_EXCEEDED = 2,
     * MEMORY_LIMIT_EXCEEDED = 3,
     * OUTPUT_LIMIT_EXCEEDED = 4,
     * RUNTIME_ERROR = 5,
     * SYSTEM_ERROR = 6
     *
     * @param signal 状态吗
     * @return 状态枚举
     */
    StatusCode mapStatusCode(int signal) {

        switch (signal) {
            case 0:
                return StatusCode.JUDGE_SUCCESS;
            case 1:
            case 2:
                return StatusCode.JUDGE_TIME_OUT;
            case 3:
                return StatusCode.JUDGE_MEMORY_OUT;
            case 4:
                return StatusCode.JUDGE_OUTPUT_OUT;
            case 5:
                return StatusCode.JUDGE_RUNTIME_ERROR;
            default:
                return StatusCode.JUDGE_SYSTEM_ERROR;
        }
    }
}
