package cn.sdu.oj.handler;

import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;


public class JwtRefreshHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private static final int tokenRefreshInterval = 24;  //刷新间隔1天

    public JwtRefreshHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String tokenHeader = request.getHeader(jwtUtil.TOKEN_HEADER);
        boolean shouldRefresh = shouldTokenRefresh(jwtUtil.getIssuedAt(tokenHeader.replace(jwtUtil.TOKEN_PREFIX, "")));
        if (shouldRefresh) {
            String roles = authentication.getAuthorities().stream()
                    .map(Object::toString).collect(Collectors.joining(","));
            String newToken = jwtUtil.createToken(authentication.getName(), ((User) authentication.getPrincipal()).getId(), roles);
            response.setHeader("Authorization", newToken);
        }
    }

    protected boolean shouldTokenRefresh(Date issueAt) {
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusHours(tokenRefreshInterval).isAfter(issueTime);
    }

}