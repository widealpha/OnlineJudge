package cn.sdu.judge.listener;

import cn.sdu.judge.bean.JudgeTask;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 判题请求的处理器
 */
@Component
@RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}")
public class JudgeMsgListener {
    private static final Logger logger = LoggerFactory.getLogger(JudgeMsgListener.class);

    @RabbitHandler
    public void process(JudgeTask task, Channel channel, Message message) throws IOException {
        logger.info("receive: " + task);
        //进行业务逻辑处理
        //手工ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
}

