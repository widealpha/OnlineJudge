package cn.sdu.oj.controller;

import cn.sdu.oj.domain.dto.MinorUserInfoDto;
import cn.sdu.oj.domain.dto.UserInfoDto;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.Pager;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.service.AdminService;
import cn.sdu.oj.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Api(value = "管理员接口", tags = "管理员接口")
public class AdminController {
    @Resource
    AdminService adminService;

    @ApiOperation("添加教师权限|ADMIN+")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/addTeacherRole")
    public ResultEntity<Boolean> addTeacherRole(
            @ApiParam("需要更改权限的用户Id") @RequestParam int userId) {
        return adminService.addTeacherRole(userId);
    }

    @ApiOperation("移除教师权限|ADMIN+")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/removeTeacherRole")
    public ResultEntity<Boolean> removeTeacherRole(
            @ApiParam("需要更改权限的用户Id") @RequestParam int userId) {
        return adminService.removeTeacherRole(userId);
    }

    @ApiOperation("添加管理员权限|ADMIN+")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/addAdminRole")
    public ResultEntity<Boolean> addAdminRole(
            @ApiParam("需要更改权限的用户Id") @RequestParam int userId) {
        return adminService.addAdminRole(userId);
    }

    @ApiOperation("移除管理员权限|ADMIN+")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/removeAdminRole")
    public ResultEntity<Boolean> removeAdminRole(
            @ApiParam("需要更改权限的用户Id") @RequestParam int userId) {
        return adminService.removeAdminRole(userId);
    }

    @ApiOperation("添加学生权限|ADMIN+")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/addStudentRole")
    public ResultEntity<Boolean> addStudentRole(
            @ApiParam("需要更改权限的用户Id") @RequestParam int userId) {
        return adminService.addStudentRole(userId);
    }

    @ApiOperation("移除学生权限|ADMIN+")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping("/removeStudentRole")
    public ResultEntity<Boolean> removeStudentRole(
            @ApiParam("需要更改权限的用户Id") @RequestParam int userId) {
        return adminService.removeStudentRole(userId);
    }


    @ApiOperation("获取系统中所有的管理员|ADMIN+")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM')")
    @RequestMapping("/allAdmins")
    public ResultEntity<Pager<UserInfoDto>> addAdmins(
            @ApiParam("分页大小") @RequestParam int size,
            @ApiParam("需要的分页") @RequestParam int page) {
        return adminService.allAdmins(size, page);
    }


    @ApiOperation("通过邮箱搜索系统中的用户|ADMIN+")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM')")
    @RequestMapping("/searchUserByEmail")
    public ResultEntity<List<MinorUserInfoDto>> searchUserByEmail(
            @ApiParam("邮箱") @RequestParam String email) {
        return adminService.searchUserByEmail(email);
    }

    @ApiOperation("通过姓名搜索系统中的用户|ADMIN+")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM')")
    @RequestMapping("/searchUserByName")
    public ResultEntity<List<MinorUserInfoDto>> searchUserByName(
            @ApiParam("邮箱") @RequestParam String name) {
        return adminService.searchUserByName(name);
    }

    @ApiOperation("向系统中导入用户|ADMIN+")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM')")
    @RequestMapping("/importUserToSystem")
    public ResultEntity<Boolean> importUserToSystem() {
        return adminService.importUserToSystem();
    }
}
