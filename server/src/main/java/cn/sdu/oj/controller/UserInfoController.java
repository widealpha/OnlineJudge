package cn.sdu.oj.controller;

import cn.sdu.oj.domain.dto.UserInfoDto;
import cn.sdu.oj.domain.po.UserInfo;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
@Api(value = "用户信息接口", tags = "用户接口")
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("用户信息")
    @PostMapping("info")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity<UserInfoDto> userInfo(@ApiIgnore @AuthenticationPrincipal User user) {
        return userInfoService.userInfo(user.getId());
    }
}
