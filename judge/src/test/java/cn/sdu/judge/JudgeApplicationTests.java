package cn.sdu.judge;

import cn.sdu.judge.bean.JudgeTask;
import cn.sdu.judge.bean.LanguageEnum;
import cn.sdu.judge.entity.ResultEntity;
import cn.sdu.judge.gateway.SftpGateway;
import cn.sdu.judge.service.JudgeService;
import cn.sdu.judge.util.FileUtil;
import cn.sdu.judge.util.SftpFileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.sftp.session.SftpFileInfo;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class JudgeApplicationTests {
    @Resource
    JudgeService judgeService;

    @Test
    void contextLoads() {
        try {
            JudgeTask judgeTask = new JudgeTask();
            judgeTask.setProblemId(0);
            judgeTask.setTaskId(UUID.randomUUID().toString());
            LanguageEnum language = LanguageEnum.PYTHON3;
            judgeTask.setCode(code(language));
            judgeTask.setLanguage(language);
            ResultEntity resultEntity = judgeService.judgeProblem(judgeTask);
            System.out.println(resultEntity);
            assert resultEntity.getCode() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String code(LanguageEnum language) {
        switch (language) {
            case C99:
                return "#include <stdio.h>\n" +
                        "int main() {\n" +
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
