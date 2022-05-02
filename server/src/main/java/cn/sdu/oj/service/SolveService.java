package cn.sdu.oj.service;

import cn.sdu.oj.dao.SolveRecordMapper;
import cn.sdu.oj.domain.bo.JudgeTask;
import cn.sdu.oj.domain.po.SolveRecord;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SolveService {
    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    SolveRecordMapper solveRecordMapper;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    public ResultEntity trySolveProblem(JudgeTask task, int userId) {
        try {
            //如果还没有判完的题目超出判题限制，拒绝判题
            if (solveRecordMapper.unfinishedSolveCount(userId) >= 3) {
                return ResultEntity.data(StatusCode.OVER_SOLVE_LIMIT);
            }
            SolveRecord solveRecord = new SolveRecord();
            solveRecord.setProblemId(task.getProblemId());
            solveRecord.setUserId(userId);
            solveRecord.setCode(task.getCode());
            solveRecord.setLanguage(task.getLanguage().name());
            solveRecordMapper.insertRecord(solveRecord);
            //获取插入数据库返回的Id
            int recordId = solveRecord.getId();
            //插入成功后，将插入的数据的id作为taskId发送到mq
            task.setTaskId("" + recordId);
            rabbitTemplate.convertAndSend(routingKey, task);
            return ResultEntity.success();
        } catch (Exception e) {
            //可能是在数据库插入失败
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }

    }
}
