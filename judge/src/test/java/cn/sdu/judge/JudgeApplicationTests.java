package cn.sdu.judge;

import cn.sdu.judge.bean.JudgeLimit;
import cn.sdu.judge.bean.JudgeTask;
import cn.sdu.judge.bean.LanguageEnum;
import cn.sdu.judge.entity.ResultEntity;
import cn.sdu.judge.entity.StatusCode;
import cn.sdu.judge.service.JudgeTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.UUID;

@SpringBootTest
class JudgeApplicationTests {
    @Resource
    JudgeTaskService judgeTaskService;

    @Test
    void contextLoads() {
        try {
            JudgeTask judgeTask = new JudgeTask();
            judgeTask.setProblemId(42);
            judgeTask.setTaskId(UUID.randomUUID().toString());
            LanguageEnum language = LanguageEnum.C99;
            judgeTask.setCode(code(language));
            judgeTask.setLanguage(language);
            judgeTask.setSpecialJudge(false);
            JudgeLimit judgeLimit = new JudgeLimit();
            judgeLimit.setMemory(1000);
            judgeLimit.setCpuTime(10);
            judgeLimit.setRealTime(10);
            judgeTask.setLimit(judgeLimit);
            ResultEntity resultEntity = judgeTaskService.judgeProblem(judgeTask);
            System.out.println(resultEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String code(LanguageEnum language) {
        switch (language) {
            case C99:
                return "#include <stdio.h>\n" +
                        "#include <unistd.h>\n" +
                        "int main() {\n" +
                        "    sleep(1);" +
                        "    char arr[100];" +
                        "    scanf(\"%s\",arr);" +
                        "    printf(\"%s\", arr);" +
                        "    return 0;\n" +
                        "}";
            case CPP17:
                return "#include <iostream>\n int main(){std::string s;std::cin>>s;std::cout << s;}";
            case JAVA8:
                return "import java.util.*;\n" +
                        "public class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        String s;\n" +
                        "        Scanner sc = new Scanner(System.in);\n" +
                        "        s = sc.next();\n" +
                        "        System.out.print(s);\n" +
                        "    }\n" +
                        "}";
            case PYTHON3:
                return "s = input()\n" +
                        "print(s, end='')";
            default:
                return "";
        }
    }

}
