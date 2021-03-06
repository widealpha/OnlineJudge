package cn.sdu.oj.config;

import cn.sdu.oj.filter.UsernamePasswordAuthenticationFilter;
import cn.sdu.oj.handler.HttpStatusLoginFailureHandler;
import cn.sdu.oj.handler.UsernameLoginSuccessHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;

@Configuration
public class UsernameLoginConfig<T extends UsernameLoginConfig<T, B>, B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<T, B> {

    private final UsernamePasswordAuthenticationFilter authFilter;

    public UsernameLoginConfig() {
        this.authFilter = new UsernamePasswordAuthenticationFilter();
    }

    @Override
    public void configure(B http) {
        //设置Filter使用的AuthenticationManager,这里取公共的即可
        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        //设置失败的Handler
        authFilter.setAuthenticationFailureHandler(new HttpStatusLoginFailureHandler());
        //不将认证后的context放入session
        authFilter.setSessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy());

        UsernamePasswordAuthenticationFilter filter = postProcess(authFilter);
        //指定Filter的位置，因为登录/user/login在spring security的白名单中，因此需要教导默认的servlet的拦截器中
        http.addFilterAfter(filter, LogoutFilter.class);
    }

    //设置成功的Handler，这个handler定义成Bean，所以从外面set进来
    public UsernameLoginConfig<T, B> loginSuccessHandler(AuthenticationSuccessHandler authSuccessHandler) {
        authFilter.setAuthenticationSuccessHandler(authSuccessHandler);
        return this;
    }

}