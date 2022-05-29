package cn.sdu.oj.service;

import cn.sdu.oj.dao.RoleMapper;
import cn.sdu.oj.dao.UserInfoMapper;
import cn.sdu.oj.dao.UserMapper;
import cn.sdu.oj.domain.dto.MinorUserInfoDto;
import cn.sdu.oj.domain.dto.UserInfoDto;
import cn.sdu.oj.domain.po.UserInfo;
import cn.sdu.oj.domain.po.UserRole;
import cn.sdu.oj.domain.vo.RoleEnum;
import cn.sdu.oj.entity.Pager;
import cn.sdu.oj.entity.ResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserMapper userMapper;

    public ResultEntity<Boolean> addTeacherRole(int userId) {
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_TEACHER.name());
        userRole.setUserId(userId);
        return ResultEntity.data(roleMapper.addUserRole(userRole));
    }

    public ResultEntity<Boolean> addStudentRole(int userId) {
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_STUDENT.name());
        userRole.setUserId(userId);
        return ResultEntity.data(roleMapper.addUserRole(userRole));
    }

    public ResultEntity<Boolean> addAdminRole(int userId) {
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_ADMIN.name());
        userRole.setUserId(userId);
        return ResultEntity.data(roleMapper.addUserRole(userRole));
    }

    public ResultEntity<Boolean> removeTeacherRole(int userId) {
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_TEACHER.name());
        userRole.setUserId(userId);
        return ResultEntity.data(roleMapper.removeUserRole(userRole));
    }

    public ResultEntity<Boolean> removeStudentRole(int userId) {
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_STUDENT.name());
        userRole.setUserId(userId);
        return ResultEntity.data(roleMapper.removeUserRole(userRole));
    }

    public ResultEntity<Boolean> removeAdminRole(int userId) {
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_ADMIN.name());
        userRole.setUserId(userId);
        return ResultEntity.data(roleMapper.removeUserRole(userRole));
    }

    public ResultEntity<Pager<UserInfoDto>> allAdmins(int size, int page) {
        Pager<UserInfoDto> pager = new Pager<>();
        pager.setTotal(roleMapper.countAllUserWithRole(RoleEnum.ROLE_ADMIN.name()));
        pager.setPage(page);
        pager.setSize(size);
        List<UserInfoDto> userInfos = new ArrayList<>();
        List<UserRole> roleList = roleMapper.selectAllUserWithRole(RoleEnum.ROLE_ADMIN.name(), (page - 1) * size, size);
        for (UserRole userRole : roleList) {
            userInfos.add(userInfoService.userInfo(userRole.getUserId()).getData());
        }
        pager.setRows(userInfos);
        return ResultEntity.data(pager);
    }


    public ResultEntity<List<MinorUserInfoDto>> searchUserByEmail(String email) {
        List<UserInfo> allInfos = userInfoMapper.selectUserByEmail(email);
        List<MinorUserInfoDto> infos = new ArrayList<>();
        for (UserInfo info : allInfos) {
            infos.add(new MinorUserInfoDto(info, userMapper.selectUsernameById(info.getUserId())));
        }
        return ResultEntity.data(infos);
    }

    public ResultEntity<List<MinorUserInfoDto>> searchUserByName(String name) {
        List<UserInfo> allInfos = userInfoMapper.selectUserByName(name);
        List<MinorUserInfoDto> infos = new ArrayList<>();
        for (UserInfo info : allInfos) {
            infos.add(new MinorUserInfoDto(info, userMapper.selectUsernameById(info.getUserId())));
        }
        return ResultEntity.data(infos);
    }

    public ResultEntity<Boolean> importUserToSystem(){
        return ResultEntity.data(true);
    }
}
