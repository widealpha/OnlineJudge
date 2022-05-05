package cn.sdu.oj.filter;

import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.util.JwtUtil;
import cn.sdu.oj.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RequestHeaderRequestMatcher requiresAuthenticationRequestMatcher;
    private List<RequestMatcher> permissiveRequestMatchers;
    private AuthenticationManager authenticationManager;

    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    JwtUtil jwtUtil;

    public JwtAuthenticationFilter() {
        //拦截header中带Authorization的请求
        this.requiresAuthenticationRequestMatcher = new RequestHeaderRequestMatcher("Authorization");
    }

    protected String getJwtToken(HttpServletRequest request) {
        return request.getHeader(jwtUtil.TOKEN_HEADER);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //不需要鉴权的请求直接放行
        if (!requiresAuthentication(request)) {
            chain.doFilter(request, response);
            return;
        }
        String tokenHeader = getJwtToken(request);
        //token为空直接放行
        if (tokenHeader == null || !tokenHeader.startsWith(jwtUtil.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            String token = tokenHeader.replace(jwtUtil.TOKEN_PREFIX, "");
            if (!jwtUtil.isExpiration(token)) {
                Date date = jwtUtil.getIssuedAt(token);
                String logoutTime = redisUtil.get("logout:" + jwtUtil.getUserId(token));
                // 判断用户退出的时间，退出之后所有签发在退出之前的token失效
                if (logoutTime != null && date.before(new Date(Long.parseLong(logoutTime)))){
                    throw new JwtException("token失效");
                }
                UsernamePasswordAuthenticationToken authentication = getAuthentication(tokenHeader);
                if (authentication == null){
                    throw new JwtException("token不完整");
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
                //鉴权完成继续交由责任链处理
                chain.doFilter(request, response);
            }
        } catch (ExpiredJwtException e) {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Content-Type", "text/html;charset=UTF-8");
            response.getWriter().print(ResultEntity.error(StatusCode.USER_TOKEN_OVERDUE));
        } catch (JwtException e) {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Content-Type", "text/html;charset=UTF-8");
            response.getWriter().print(ResultEntity.error(StatusCode.USER_TOKEN_ERROR));
        } catch (Exception e) {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setHeader("Content-Type", "text/html;charset=UTF-8");
            response.getWriter().print(ResultEntity.error(StatusCode.COMMON_FAIL));
        }
    }

    //获取用户信息
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) {
        String token = tokenHeader.replace(jwtUtil.TOKEN_PREFIX, "");
        String username = jwtUtil.getUsername(token);
        Integer userId = jwtUtil.getUserId(token);
        if (username == null || userId == null) {
            return null;
        }
        // 获得权限 添加到权限上去
        String roles = jwtUtil.getUserRole(token);
        User user = new User(userId, username, "[PROTECTED]", AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
        return new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
    }

    protected boolean requiresAuthentication(HttpServletRequest request) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    protected boolean permissiveRequest(HttpServletRequest request) {
        if (permissiveRequestMatchers == null)
            return false;
        for (RequestMatcher permissiveMatcher : permissiveRequestMatchers) {
            if (permissiveMatcher.matches(request))
                return true;
        }
        return false;
    }

    public void setPermissiveUrl(String... urls) {
        if (permissiveRequestMatchers == null)
            permissiveRequestMatchers = new ArrayList<>();
        for (String url : urls)
            permissiveRequestMatchers.add(new AntPathRequestMatcher(url));
    }

    public void setAuthenticationSuccessHandler(
            AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(
            AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    protected AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    protected AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }
}