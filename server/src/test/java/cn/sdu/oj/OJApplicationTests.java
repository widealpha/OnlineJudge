package cn.sdu.oj;

import cn.sdu.oj.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OJApplicationTests {
    @Autowired
    JwtUtil jwtUtil;
    @Test
    void contextLoads() {
//        System.out.println(problemController.getProblemByTagName("数组",1,10));
//        System.out.println(ProblemUtil.service.getClass().getName());
    }

}
