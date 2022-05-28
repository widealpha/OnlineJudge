package cn.sdu.oj.controller;

import cn.sdu.oj.config.UsernameLoginConfig;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.filter.UsernamePasswordAuthenticationFilter;
import cn.sdu.oj.service.UserGroupService;
import cn.sdu.oj.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

    @ApiOperation("用户组创建") //创建用户组，父用户组id为可选   //老师或者管理员可以使用
    @PostMapping("/createUserGroup")
    public ResultEntity createUserGroup(
            @ApiParam(value = "姓名") @RequestParam String name,
            @ApiParam(value = "类型") @RequestParam String type,
            @ApiParam(value = "简介") @RequestParam String introduction,
            @ApiParam(value = "父用户组") @RequestParam(required = false) Integer fatherId,
            @ApiIgnore @AuthenticationPrincipal User user) {
        try {
            if (fatherId == null) {  //创建新用户组
                String a = UserGroupService.createUserGroup(name, type, introduction, fatherId, user.getId()).toString();
                return ResultEntity.success("创建新用户组成功", a);
            } else {  //为一个用户组创建子用户组
                //要求：该 用户组目前不含人 ，且是我创建
                if (UserGroupService.getUserGroupInfoById(fatherId).getCreatorId() != user.getId()) {
                    return ResultEntity.error(StatusCode.NO_PERMISSION);
                } else if (UserGroupService.getUserGroupMembers(fatherId).size() != 0) {
                    return ResultEntity.error("无法为已有成员的用户组添加子用户组");
                }
                Integer id = UserGroupService.createUserGroup(name, type, introduction, fatherId, user.getId());

                UserGroupService.updateChildrenUserGroup(fatherId, id);
                return ResultEntity.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.error(StatusCode.COMMON_FAIL);
        }
    }

    @ApiOperation("用户组信息获取") //获取用户组信息，只包含本组信息   //创建者（老师或者管理员）或者用户组成员可以使用
    @PostMapping("/getUserGroupInfo")
    public ResultEntity getUserGroupInfo(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id) {
        return ResultEntity.data(UserGroupService.getUserGroupInfoById(id));
    }

    @ApiOperation("用户组删除") //删除用户组信息，和成员信息   //创建者（老师或者管理员）可以使用
    @PostMapping("/deleteUserGroup")
    public ResultEntity deleteUserGroup(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data(UserGroupService.deleteUserGroup(user.getId(), id));
    }

    @ApiOperation("用户组信息修改") //修改用户组信息（名称和简介） //创建者（老师或者管理员）可以使用
    @PostMapping("/alterUserGroupInfo")
    public ResultEntity alterUserGroupInfo(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id,
            @ApiParam(value = "用户组名称") @RequestParam(required = true) String name,
            @ApiParam(value = "用户组简介") @RequestParam(required = true) String introduction,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data(UserGroupService.alterUserGroupInfo(user.getId(), id, name, introduction));
    }

    @ApiOperation("获取我创建的用户组列表")   //所有人可以使用
    @PostMapping("/getSelfCreatedUserGroup")
    public ResultEntity getSelfCreatedUserGroup(
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data(UserGroupService.getSelfCreatedUserGroup(user.getId()));
    }

    @ApiOperation("向我创建的用户组加人")   //创建者（老师或者管理员）可以使用
    @PostMapping("/addMemberToUserGroup")
    public ResultEntity addMemberToUserGroup(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id,
            @ApiParam(value = "人员ID列表,请使用JSONArray") @RequestParam(required = true) String member,
            @ApiIgnore @AuthenticationPrincipal User user) {
        JSONArray jsonArray = JSON.parseArray(member);
        if (jsonArray.contains(user.getId())) {
            return ResultEntity.error("创建者已在用户组内！");
        }
        return ResultEntity.data(UserGroupService.addMemberToUserGroup(user.getId(), id, jsonArray));
    }

    @ApiOperation("从我创建的用户组删人")   //创建者（老师或者管理员）可以使用
    @PostMapping("/deleteUserGroupMember")
    public ResultEntity deleteUserGroupMember(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id,
            @ApiParam(value = "人员ID列表,请使用JSONArray") @RequestParam(required = true) String member,
            @ApiIgnore @AuthenticationPrincipal User user) {
        JSONArray jsonArray = JSON.parseArray(member);
        return ResultEntity.data(UserGroupService.deleteUserGroupMember(user.getId(), id, jsonArray));
    }

    @ApiOperation("查询我加入的用户组")  //我是用户组成员   所有人都可使用
    @PostMapping("/getSelfJoinedUserGroup")
    public ResultEntity getSelfJoinedUserGroup(
            @ApiIgnore @AuthenticationPrincipal User user) {
        return ResultEntity.data(UserGroupService.getSelfJoinedUserGroup(user.getId()));
    }

    @ApiOperation("查询我创建的用户组人员ID列表")  // 创建者（老师或者管理员）可以使用
    @PostMapping("/getUserGroupMembers")    //只能查询最底层用户组
    public ResultEntity getUserGroupMembers(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id,
            @ApiIgnore @AuthenticationPrincipal User user) {
        if (UserGroupService.getUserGroupInfoById(id).getCreatorId().equals(user.getId())) {
            return ResultEntity.data(UserGroupService.getUserGroupMembers(id));
        } else return ResultEntity.data(StatusCode.NO_PERMISSION);
    }

    //获取一个用户组有的题目集
    @ApiOperation("获取一个用户组有的题目集")  // 创建者（老师）可以使用
    @PostMapping("/getUserGroupProblemSet")    //获取一个用户组有的题目集
    public ResultEntity getUserGroupProblemSet(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer id,
            @ApiIgnore @AuthenticationPrincipal User user) {
        if (UserGroupService.getUserGroupInfoById(id).getCreatorId().equals(user.getId())) {
            return ResultEntity.data(UserGroupService.getUserGroupProblemSet(id));
        } else return ResultEntity.data(StatusCode.NO_PERMISSION);
    }

    //获取一个题目集

    //为一个用户组添加题目集
    @ApiOperation("为一个用户组添加题目集")  // 创建者（老师）可以使用
    @PostMapping("/addUserGroupProblemSet")    //为一个用户组添加题目集
    public ResultEntity addUserGroupProblemSet(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer userGroupId,
            @ApiParam(value = "题目集id") @RequestParam(required = true) Integer problemSetId,
            @ApiIgnore @AuthenticationPrincipal User user) {
        if (UserGroupService.getUserGroupInfoById(userGroupId).getCreatorId().equals(user.getId())) {
            List<Integer> list = UserGroupService.getUserGroupProblemSet(userGroupId);
            for (Integer i : list
            ) {
                if (i == problemSetId) {
                    return ResultEntity.error("该题目集已在用户组里");
                }
            }
            return ResultEntity.data(UserGroupService.addUserGroupProblemSet(userGroupId, problemSetId));
        } else return ResultEntity.data(StatusCode.NO_PERMISSION);
    }

    //为一个用户组删除题目集
    @ApiOperation("为一个用户组删除题目集")  // 创建者（老师）可以使用
    @PostMapping("/deleteUserGroupProblemSet")    //为一个用户组删除题目集
    public ResultEntity deleteUserGroupProblemSet(
            @ApiParam(value = "用户组id") @RequestParam(required = true) Integer userGroupId,
            @ApiParam(value = "题目集id") @RequestParam(required = true) Integer problemSetId,
            @ApiIgnore @AuthenticationPrincipal User user) {
        if (UserGroupService.getUserGroupInfoById(userGroupId).getCreatorId().equals(user.getId())) {
            List<Integer> list = UserGroupService.getUserGroupProblemSet(userGroupId);
            for (Integer i : list
            ) {
                if (i == problemSetId) {

                    return ResultEntity.data(UserGroupService.deleteUserGroupProblemSet(userGroupId, problemSetId));
                }
            }
            return ResultEntity.error("题目集不在用户组里");
        } else return ResultEntity.data(StatusCode.NO_PERMISSION);
    }

}
