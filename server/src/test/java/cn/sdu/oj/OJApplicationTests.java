package cn.sdu.oj;

import cn.sdu.oj.util.JwtUtil;
import cn.sdu.oj.util.StringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class OJApplicationTests {
    @Autowired
    JwtUtil jwtUtil;

    @Test
    void contextLoads() {
        String s = UUID.randomUUID().toString().replace("-", "");
        System.out.println(StringUtil.allLetter(s));
    }

}
