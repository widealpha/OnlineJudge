package cn.sdu.judge.controller;

import cn.sdu.judge.bean.JudgeLimit;
import cn.sdu.judge.bean.JudgeTask;
import cn.sdu.judge.bean.LanguageEnum;
import cn.sdu.judge.entity.ResultEntity;
import cn.sdu.judge.entity.StatusCode;
import cn.sdu.judge.service.JudgeTaskService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
public class JudgeController {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private JudgeTaskService judgeTaskService;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    @RequestMapping("/judge")
    public String judge() {
        JudgeTask judgeTask = new JudgeTask();
        judgeTask.setProblemId(0);
        judgeTask.setTaskId(UUID.randomUUID().toString());
        LanguageEnum language = LanguageEnum.JAVA8;
        judgeTask.setCode(code(language));
        judgeTask.setLanguage(language);
        judgeTask.setSpecialJudge(false);
        rabbitTemplate.convertAndSend("solve-request", judgeTask);
        return "ok";
    }

    String code(LanguageEnum language) {
        switch (language) {
            case C99:
                return "#include <stdio.h>\n" + "int main() {\n" + "    char arr[100];" + "    scanf(\"%s\",arr);" + "    printf(\"%s\", arr);" + "    return 0;\n" + "}";
            case CPP17:
                return "#include <iostream>\n int main(){std::string s;std::cin>>s;std::cout << s;}";
            case JAVA8:
                return "import java.util.*;\n" + "public class Main {\n" + "    public static void main(String[] args) {\n" + "        String s;\n" + "        Scanner sc = new Scanner(System.in);\n" + "        s = sc.next();\n" + "        System.out.println(s);\n" + "    }\n" + "}";
            case PYTHON3:
                return "s = input()\n" + "print(s, end='')";
            default:
                return "";
        }
    }

}
