package cn.sdu.judge.service;

import cn.sdu.judge.bean.*;
import cn.sdu.judge.entity.ResultEntity;
import cn.sdu.judge.entity.StatusCode;
import cn.sdu.judge.exceptions.CurrentNotSupportException;
import cn.sdu.judge.gateway.SftpGateway;
import cn.sdu.judge.judger.CPPJudgeImpl;
import cn.sdu.judge.judger.JavaJudgeImpl;
import cn.sdu.judge.judger.JudgeInterface;
import cn.sdu.judge.util.FileUtil;
import cn.sdu.judge.util.SftpFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class JudgeService {
    Logger logger = LoggerFactory.getLogger(JudgeService.class);
    @Resource
    SftpFileUtil sftpFileUtil;

    public ResultEntity judgeProblem(JudgeTask task) throws IOException {
        try {
            List<Checkpoint> checkpointList = sftpFileUtil.checkpoints(task.getProblemId());
            if (checkpointList == null || checkpointList.isEmpty()) {
                logger.warn("判题机错误或远程数据不存在");
                return ResultEntity.data(StatusCode.NO_DATA_EXIST);
            }
            JudgeInterface judge = fetchJudgeInterface(task.getLanguage());
            CompileInfo compileInfo = judge.compile(task.getCode());
            if (compileInfo.isSuccess()) {
                for (Checkpoint checkpoint : checkpointList) {
                    RunInfo runInfo = judge.run(checkpoint);
                    if (!runInfo.isSuccess()) {
                        if (runInfo.getExitCode() == -1) {
                            return ResultEntity.data(StatusCode.RUN_ERROR, runInfo);
                        } else {
                            runInfo.setCheckpoint(checkpoint);
                            return ResultEntity.data(StatusCode.CHECKPOINT_ERROR, runInfo);
                        }
                    }
                    logger.info(runInfo.toString());
                }
            } else {
                return ResultEntity.data(StatusCode.COMPILE_ERROR, compileInfo);
            }
        } catch (Exception e) {
            logger.error("判题机错误或远程数据不存在: ", e);
            return ResultEntity.data(StatusCode.PROBLEM_NOT_EXIST);
        }
        return ResultEntity.success();
    }

    JudgeInterface fetchJudgeInterface(LanguageEnum language) throws CurrentNotSupportException, IOException {
        switch (language) {
            case CPP17:
                return new CPPJudgeImpl();
            case JAVA8:
                return new JavaJudgeImpl();
            default:
                throw new CurrentNotSupportException(language);
        }
    }
}
