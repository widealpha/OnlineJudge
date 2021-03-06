package cn.sdu.oj.handler;

import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败处理器，返回登陆失败的http代码
 */
public class HttpStatusLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSON.toJSONString(ResultEntity.error(StatusCode.USER_CREDENTIALS_ERROR)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}