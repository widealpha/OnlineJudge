package cn.sdu.oj.controller;

import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public ResultEntity register(@RequestParam String username, @RequestParam String password) {
        return ResultEntity.data(userService.register(username, password, null), null);
    }

    @RequestMapping("/email_register")
    public ResultEntity mailRegister(@RequestParam String email, @RequestParam String code, @RequestParam String password) {
        return ResultEntity.data(userService.emailRegister(email, code, password), null);
    }

    @RequestMapping("/send_validate_code")
    public ResultEntity sendValidateCode(@RequestParam String email) {
        userService.sendRegisterValidateCode(email);
        return ResultEntity.data(StatusCode.SUCCESS, null);
    }
}
