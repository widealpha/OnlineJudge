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
@RequestMapping("/usergroup")
@Api(value = "用户组接口", tags = "用户组接口")
public class UsergroupController {                  // TODO 权限
    @Autowired
    private UserGroupService userGroupService;

    @ApiOperation("用户组创建") //创建用户组，父用户组id为可选
    @PostMapping("/createUsergroup")
    public ResultEntity createUsergroup(
            @ApiParam(value = "姓名") @RequestParam String name,
            @ApiParam(value = "类型") @RequestParam String type,
            @ApiParam(value = "简介") @RequestParam  String introduce,
            @ApiParam(value = "父用户组") @RequestParam(required = false) Integer fatherId ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data(userGroupService.createUsergroup(name, type, introduce,fatherId,user.getId()), null);
    }

    @ApiOperation("用户组信息获取") //获取用户组信息，只包含本组信息
    public ResultEntity getUsergroup(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ) {
        return ResultEntity.data( userGroupService.getUsergroupById(id));
    }

    @ApiOperation("用户组删除") //删除用户组信息，和成员信息
    public ResultEntity deleteUsergroup(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( userGroupService.deleteUsergroup(user.getId(),id));
    }


    @ApiOperation("用户组信息修改") //修改用户组信息（名称和简介）
    public ResultEntity alterUsergroupInfo(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiParam(value = "用户组名称") @RequestParam(required = true) String name ,
            @ApiParam(value = "用户组简介") @RequestParam(required = true) String introduce ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( userGroupService.alterUsergroupInfo(user.getId(),id,name,introduce));
    }

    @ApiOperation("获取我创建的用户组列表")
    public ResultEntity getSelfCreatedUsergroup(
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( userGroupService.getSelfCreatedUsergroup(user.getId()));
    }

    @ApiOperation("向我创建的用户组加人")
    public ResultEntity addMemberToUsergroup(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiParam(value = "人员ID列表") @RequestParam(required = true) List<Integer> member ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( userGroupService.addMemberToUsergroup(user.getId(),id,member));
    }

    @ApiOperation("从我创建的用户组删人")
    public ResultEntity deleteUsergroupMember(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiParam(value = "人员ID列表") @RequestParam(required = true) List<Integer> member ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( userGroupService.deleteUsergroupMember(user.getId(),id,member));
    }

    @ApiOperation("查询我加入的用户组")
    public ResultEntity getSelfJoinedUsergroup(
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data( userGroupService.getSelfJoinedUsergroup(user.getId()));
    }

    @ApiOperation("查询我创建的用户组人员ID列表")
    public ResultEntity getUsergroupMembers(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id ,
            @ApiIgnore @AuthenticationPrincipal User user) {
        if (userGroupService.getUsergroupById(id).getCreatorId().equals(user.getId())){
            return ResultEntity.data( userGroupService.getUsergroupMembers(user.getId()));
        }
        else return ResultEntity.data(StatusCode.NO_PERMISSION);
    }
}
