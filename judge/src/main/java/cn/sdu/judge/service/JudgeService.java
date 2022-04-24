package cn.sdu.judge.service;

import cn.sdu.judge.bean.JudgeTask;
import cn.sdu.judge.entity.ResultEntity;
import cn.sdu.judge.gateway.SftpGateway;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JudgeService {
    @Resource
    private SftpGateway sftpGateway;

    ResultEntity judgeProblem(JudgeTask task) {
        sftpGateway.listFile("./");

        return ResultEntity.success();
    }
}
