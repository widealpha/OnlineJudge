package cn.sdu.oj.handler;

import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.util.JwtUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class UsernameLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public UsernameLoginSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        //生成token，并把token加密相关信息缓存
        //生成以','分割的角色信息
        String roles = authentication.getAuthorities().stream()
                .map(Object::toString).collect(Collectors.joining(","));
        String token = jwtUtil.createToken(authentication.getName(), ((User) authentication.getPrincipal()).getId(), roles);
        response.setHeader("Authorization", "Bearer " + token);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().print(JSON.toJSONString(ResultEntity.data(token)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}