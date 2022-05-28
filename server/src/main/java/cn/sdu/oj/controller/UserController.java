package cn.sdu.oj.controller;

import cn.sdu.oj.config.UsernameLoginConfig;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.filter.UsernamePasswordAuthenticationFilter;
import cn.sdu.oj.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Api(value = "用户接口", tags = "用户接口")
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ResultEntity<Boolean> register(
            @ApiParam(value = "用户名") @RequestParam String username,
            @ApiParam(value = "密码") @RequestParam String password) {
        return userService.register(username, password, null);
    }

    @ApiOperation("邮箱注册")
    @PostMapping("/email_register")
    public ResultEntity<Boolean> mailRegister(
            @ApiParam(value = "邮箱") @RequestParam String email,
            @ApiParam(value = "验证码") @RequestParam String code,
            @ApiParam(value = "密码") @RequestParam String password) {
        return userService.emailRegister(email, code, password);
    }

    @ApiOperation("发送邮箱验证码")
    @PostMapping("/send_validate_code")
    public ResultEntity<Void> sendValidateCode(@ApiParam(value = "邮箱") @RequestParam String email) {
        userService.sendRegisterValidateCode(email);
        return ResultEntity.data(StatusCode.SUCCESS, null);
    }

    @ApiOperation(value = "注销登录", authorizations = {@Authorization("common")})
    @PostMapping("/logout")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity<Boolean> logout(@ApiIgnore @AuthenticationPrincipal User user) {
        return userService.logout(user);
    }

    @ApiOperation(value = "修改密码", authorizations = {@Authorization("common")})
    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('COMMON')")
    public ResultEntity<Boolean> changePassword(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("原始密码") @RequestParam String oldPassword,
            @ApiParam("新密码") @RequestParam String newPassword) {
        return userService.changePassword(user.getId(), oldPassword, newPassword);
    }

    /**
     * 非真实接口,只用于生成文档
     * 实际登录处于拦截器层{@link UsernameLoginConfig}{@link UsernamePasswordAuthenticationFilter}
     */
    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
    })
    @PostMapping(value = "/login", produces = "application/json")
    public void login() {
    }
}
