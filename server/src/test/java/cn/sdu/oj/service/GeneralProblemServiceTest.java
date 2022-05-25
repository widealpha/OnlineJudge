package cn.sdu.oj.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GeneralProblemServiceTest {
    @Autowired
    private ProblemService service;

    @Test
    public void isProblemExist() {
        service.isProblemExist(38);
        service.isProblemExist(5555);

    }
}
