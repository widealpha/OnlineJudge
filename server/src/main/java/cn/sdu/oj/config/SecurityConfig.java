package cn.sdu.oj.config;


import cn.sdu.oj.filter.JwtAuthenticationFilter;
import cn.sdu.oj.filter.OptionsRequestFilter;
import cn.sdu.oj.handler.JwtRefreshHandler;
import cn.sdu.oj.handler.UsernameLoginSuccessHandler;
import cn.sdu.oj.service.UserDetailService;
import cn.sdu.oj.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(permissiveRequestUrls()).permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()  //CSRF禁用，因为不使用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //禁用session
                .and().formLogin().disable() //禁用form登录
                .cors().configurationSource(corsConfigurationSource()).and() //配置cors
                .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class)
                //添加登录filter
                .apply(new UsernameLoginConfig<>())
                .loginSuccessHandler(usernameLoginSuccessHandler())
                .and()
                //添加token的filter
                .apply(new JwtLoginConfig<>())
                .permissiveRequestUrls(permissiveRequestUrls()) //设置jwt拦截白名单
                .tokenValidSuccessHandler(jwtRefreshSuccessHandler())
                .and()
                //使用默认的logoutFilter
                .logout()
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()); //logout成功后返回200
    }

    /**
     * spring security拦截白名单
     *
     * @return spring security拦截白名单
     */
    private String[] permissiveRequestUrls() {
        return new String[]{
                //接口文档
                "/doc.html","/webjars/**","/img.icons/**","/swagger-resources/**","/**","/v2/api-docs",
                //注册
                "/user/register", "/user/send_validate_code", "/user/email_register",
                //登录
                "/user/login",
                //注销
                "/user/logout",
                //匿名访问
                "anonymous/**",
        };
    }

    @Bean
    JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    protected PasswordEncoder getPw() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected JwtAuthenticationFilter getJwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return new UserDetailService();
    }

    @Bean
    protected UserDetailService jwtUserService() {
        return new UserDetailService();
    }

    @Bean
    protected UsernameLoginSuccessHandler usernameLoginSuccessHandler() {
        return new UsernameLoginSuccessHandler(jwtUtil());
    }

    @Bean
    protected JwtRefreshHandler jwtRefreshSuccessHandler() {
        return new JwtRefreshHandler(jwtUtil());
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}