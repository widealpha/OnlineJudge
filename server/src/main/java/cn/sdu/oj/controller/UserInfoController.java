package cn.sdu.oj.controller;

import cn.sdu.oj.domain.dto.MinorUserInfoDto;
import cn.sdu.oj.domain.dto.UserInfoDto;
import cn.sdu.oj.domain.po.UserInfo;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("user")
@Api(value = "用户信息接口", tags = "用户接口")
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("获取他人简略用户信息")
    @PostMapping("minorInfo")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity<MinorUserInfoDto> minorUserInfo(@ApiParam("用户Id") @RequestParam int userId) {
        return userInfoService.minorUserInfo(userId);
    }

    @ApiOperation("用户信息")
    @PostMapping("info")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity<UserInfoDto> userInfo(@ApiIgnore @AuthenticationPrincipal User user) {
        return userInfoService.userInfo(user.getId());
    }

    @ApiOperation("更新用户信息")
    @PostMapping("updateInfo")
    @PreAuthorize("hasRole('COMMON')")
    ResultEntity<Boolean> updateUserInfo(
            @ApiIgnore @AuthenticationPrincipal User user,
            @ApiParam("昵称") @RequestParam("nickname") String nickname,
            @ApiParam("姓名") @RequestParam("name") String name,
            @ApiParam("头像链接") @RequestParam("avatar") String avatar) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNickname(nickname);
        userInfo.setAvatar(avatar);
        userInfo.setName(name);
        return userInfoService.updateUserInfo(userInfo);
    }


    @ApiOperation("通过用户名查找用户")
    @PreAuthorize("hasRole('COMMON')")
    @PostMapping("searchUserByUsername")
    ResultEntity<List<MinorUserInfoDto>> searchUserByUsername(
            @ApiParam("用户名") @RequestParam String username) {
        return userInfoService.searchUserByUsername(username);
    }

    @ApiOperation("通过用户昵称查找用户")
    @PreAuthorize("hasRole('COMMON')")
    @PostMapping("searchUserByUsername")
    ResultEntity<List<MinorUserInfoDto>> searchUserByNickname(
            @ApiParam("昵称") @RequestParam String nickname) {
        return userInfoService.searchUserByNickname(nickname);
    }
}
