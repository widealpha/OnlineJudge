package cn.sdu.oj.controller;

import cn.sdu.oj.config.UsernameLoginConfig;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.filter.UsernamePasswordAuthenticationFilter;
import cn.sdu.oj.service.UserGroupService;
import cn.sdu.oj.service.UserService;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


@RestController
@RequestMapping("/userGroup")
@Api(value = "用户组接口", tags = "用户组接口")
public class UserGroupController {                  // TODO 权限
    @Autowired
    private UserGroupService UserGroupService;

    @ApiOperation("用户组创建") //创建用户组，父用户组id为可选
    @PostMapping("/createUserGroup")
    public ResultEntity createUserGroup(
            @ApiParam(value = "姓名") @RequestParam String name,
            @ApiParam(value = "类型") @RequestParam String type,
            @ApiParam(value = "简介") @RequestParam  String introduction,
            @ApiParam(value = "父用户组") @RequestParam(required = false) Integer fatherId ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data(UserGroupService.createUserGroup(name, type, introduction,fatherId,user.getId()), null);
    }

    @ApiOperation("用户组信息获取") //获取用户组信息，只包含本组信息
    @PostMapping("/getUserGroup")
    public ResultEntity getUserGroup(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ) {
        return ResultEntity.data( UserGroupService.getUserGroupById(id));
    }

    @ApiOperation("用户组删除") //删除用户组信息，和成员信息
    @PostMapping("/deleteUserGroup")
    public ResultEntity deleteUserGroup(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( UserGroupService.deleteUserGroup(user.getId(),id));
    }


    @ApiOperation("用户组信息修改") //修改用户组信息（名称和简介）
    @PostMapping("/alterUserGroupInfo")
    public ResultEntity alterUserGroupInfo(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiParam(value = "用户组名称") @RequestParam(required = true) String name ,
            @ApiParam(value = "用户组简介") @RequestParam(required = true) String introduction ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( UserGroupService.alterUserGroupInfo(user.getId(),id,name,introduction));
    }

    @ApiOperation("获取我创建的用户组列表")
    @PostMapping("/getSelfCreatedUserGroup")
    public ResultEntity getSelfCreatedUserGroup(
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( UserGroupService.getSelfCreatedUserGroup(user.getId()));
    }

    @ApiOperation("向我创建的用户组加人")
    @PostMapping("/addMemberToUserGroup")
    public ResultEntity addMemberToUserGroup(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiParam(value = "人员ID列表",type = "List<Integer>") @RequestParam(required = true) List<Integer> member ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( UserGroupService.addMemberToUserGroup(user.getId(),id,member));
    }

    @ApiOperation("从我创建的用户组删人")
    @PostMapping("/deleteUserGroupMember")
    public ResultEntity deleteUserGroupMember(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiParam(value = "人员ID列表") @RequestParam(required = true) List<Integer> member ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( UserGroupService.deleteUserGroupMember(user.getId(),id,member));
    }

    @ApiOperation("查询我加入的用户组")
    @PostMapping("/getSelfJoinedUserGroup")
    public ResultEntity getSelfJoinedUserGroup(
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( UserGroupService.getSelfJoinedUserGroup(user.getId()));
    }

    @ApiOperation("查询我创建的用户组人员ID列表")
    @PostMapping("/getUserGroupM@")
    public ResultEntity getUserGroupMembers(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        if (UserGroupService.getUserGroupById(id).getCreatorId().equals(user.getId())){
            return ResultEntity.data( UserGroupService.getUserGroupMembers(user.getId()));
        }
        else return ResultEntity.data(StatusCode.NO_PERMISSION);
    }
}
