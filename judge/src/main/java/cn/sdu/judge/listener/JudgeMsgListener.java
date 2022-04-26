package cn.sdu.judge.listener;

import cn.sdu.judge.bean.JudgeTask;
import cn.sdu.judge.entity.ResultEntity;
import cn.sdu.judge.service.JudgeTaskService;
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
import javax.xml.transform.Result;
import java.io.IOException;

/**
 * 判题请求的处理器
 */
@Component
@RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
public class JudgeMsgListener {
    private static final Logger logger = LoggerFactory.getLogger(JudgeMsgListener.class);

    @Resource
    private JudgeTaskService judgeTaskService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;


    @RabbitHandler
    public void process(JudgeTask task, Channel channel, Message message) throws IOException {
        logger.info("receive: " + task);
        ResultEntity result = judgeTaskService.judgeProblem(task);
        //进行业务逻辑处理
        //手工ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        rabbitTemplate.convertAndSend(routingKey, result);
    }
}

