package cn.sdu.oj.listener;

import cn.sdu.oj.domain.bo.JudgeResult;
import cn.sdu.oj.domain.bo.JudgeStatus;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.service.SolveService;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
public class SolveMsgListener {
    private static final Logger logger = LoggerFactory.getLogger(SolveMsgListener.class);

    @Resource
    private SolveService solveService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;


    @RabbitHandler
    public void process(String result, Channel channel, Message message) throws IOException {
        logger.info("receive: " + result);
        ResultEntity entity = JSONObject.parseObject(result, ResultEntity.class);
        if (solveService.solveResultReceive(entity)){
            //进行业务逻辑处理
            //手工ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
