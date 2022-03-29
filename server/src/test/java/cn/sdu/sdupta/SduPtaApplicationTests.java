package cn.sdu.sdupta;

import cn.sdu.sdupta.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SduPtaApplicationTests {
    @Autowired
    JwtUtil jwtUtil;
    @Test
    void contextLoads() {
//        System.out.println(problemController.getProblemByTagName("数组",1,10));
//        System.out.println(ProblemUtil.service.getClass().getName());
    }

}
