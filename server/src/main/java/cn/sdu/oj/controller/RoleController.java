package cn.sdu.oj.controller;

import cn.sdu.oj.dao.UserMapper;
import cn.sdu.oj.domain.vo.RoleEnum;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.RoleService;
import cn.sdu.oj.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Arrays;

@RestController
@RequestMapping("/role")
@Api(value = "用户权限接口", tags = "权限接口")
public class RoleController {
    @Resource
    RoleService roleService;


    @ApiOperation("所有可能的身份|ADMIN|SYSTEM")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @GetMapping("/allRoles")
    ResultEntity<String> allRoles() {
        return ResultEntity.data(Arrays.toString(RoleEnum.values()));
    }

    @ApiOperation("用户添加身份|ADMIN|SYETEM")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @PostMapping("/addRole")
    ResultEntity<Boolean> addRole(@ApiParam("添加的身份") @RequestParam String role,
                                  @ApiParam("待更改权限的用户") @RequestParam int userId) {
        try {
            return roleService.addRole(userId, RoleEnum.valueOf(role));
        } catch (IllegalArgumentException e) {
            return ResultEntity.error(StatusCode.ROLE_NOT_EXIST);
        }
    }

    @ApiOperation("移除用户身份|ADMIN|SYSTEM")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @PostMapping("/removeRole")
    ResultEntity<Boolean> removeRole(@ApiParam("添加的身份") @RequestParam String role,
                                     @ApiParam("待更改权限的用户") @RequestParam int userId) {
        try {
            return roleService.removeRole(userId, RoleEnum.valueOf(role));
        } catch (IllegalArgumentException e) {
            return ResultEntity.error(StatusCode.ROLE_NOT_EXIST);
        }
    }
}
