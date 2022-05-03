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
        judgeResult.setCheckPointSize(0);

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
                StatusCode status = StatusCode.NO_DATA_EXIST;
                taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), status.getCode());
                return ResultEntity.data(status, judgeResult);
            }
            //获取判题点的数量并填充到判题结果中
            judgeResult.setCheckPointSize(checkpointList.size());
            judge = fetchJudgeInterface(task.getLanguage());
            CompileInfo compileInfo = judge.compile(task.getCode());
            taskRecord.setCompileInfo(JSON.toJSONString(compileInfo));
            if (compileInfo.isSuccess()) {
                for (int i = 0; i < checkpointList.size(); i++) {
                    Checkpoint checkpoint = checkpointList.get(i);
                    RunInfo runInfo = judge.run(checkpoint);
                    if (!runInfo.isSuccess()) {
                        StatusCode statusCode;
                        //返回码不为0,并且错误信息不为空,则认为该测试点运行时出错
                        if (runInfo.getExitCode() != 0 && runInfo.getError() != null && !runInfo.getError().isEmpty()) {
                            statusCode = StatusCode.RUN_ERROR;
                            judgeResult.getErrors().put(i, "运行时出错");
                        } else {
                            statusCode = StatusCode.CHECKPOINT_ERROR;
                            judgeResult.getErrors().put(i, "测试点未通过");
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
                StatusCode status = StatusCode.COMPILE_ERROR;
                taskRecordMapper.updateTaskRecord(taskRecord);
                taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), status.getCode());
                judgeResult.getErrors().put(-1, compileInfo.getError());
                return ResultEntity.data(status, judgeResult);
            }
        } catch (Exception e) {
            logger.error("判题机错误或远程数据不存在: ", e);
            StatusCode status = StatusCode.PROBLEM_NOT_EXIST;
            taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), status.getCode());
            judgeResult.getErrors().put(-1, e.getMessage());
            return ResultEntity.data(status, judgeResult);
        } finally {
            if (judge != null) {
                judge.clean();
            }
        }
        taskRecordMapper.updateTaskRecordStatus(taskRecord.getTaskId(), StatusCode.ALL_CHECKPOINTS_SUCCESS.getCode());
        taskRecord.setRunInfo(JSON.toJSONString(judgeResult.getDetails()));
        taskRecordMapper.updateTaskRecord(taskRecord);
        return ResultEntity.data(StatusCode.ALL_CHECKPOINTS_SUCCESS, judgeResult);
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
}
