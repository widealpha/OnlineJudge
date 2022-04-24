package cn.sdu.judge.controller;

import cn.sdu.judge.bean.JudgeLimit;
import cn.sdu.judge.bean.JudgeTask;
import cn.sdu.judge.bean.LanguageEnum;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
public class JudgeController {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    @RequestMapping("/judge")
    public String judge() {
        JudgeTask judgeTask = new JudgeTask();
        judgeTask.setTaskId(0);
        judgeTask.setProblemId(1);
        judgeTask.setCode("code");
        judgeTask.setLanguage(LanguageEnum.JAVA8);
        judgeTask.setLimit(new JudgeLimit());
        rabbitTemplate.convertAndSend(routingKey, judgeTask);
        return "ok";
    }

}
