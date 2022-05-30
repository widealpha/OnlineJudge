package cn.sdu.oj;

import cn.sdu.oj.util.JwtUtil;
import cn.sdu.oj.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class OJApplicationTests {
    @Autowired
    JwtUtil jwtUtil;

    @Test
    void contextLoads() {
//        List<String> list = new ArrayList<>();
//        list.add("A");
//        System.out.println(JSON.toJSONString(list));
//        String s = UUID.randomUUID().toString().replace("-", "");
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("C");
        list.add("B");
        String s = JSON.toJSONString(list);
        System.out.println(s);
        System.out.println(JSON.parseArray(s, String.class));
    }
}
