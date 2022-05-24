package cn.sdu.oj.service;

import cn.sdu.oj.dao.RoleMapper;
import cn.sdu.oj.dao.UserMapper;
import cn.sdu.oj.domain.po.UserRole;
import cn.sdu.oj.domain.vo.RoleEnum;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RoleService {
    @Resource
    RoleMapper roleMapper;
    @Resource
    UserMapper userMapper;

    /**
     * 管理员添加用户权限
     *
     * @param userId 需要添加权限的用户Id
     * @param role   添加的权限
     * @return 权限添加是否成功
     */
    public ResultEntity<Boolean> addRole(int userId, RoleEnum role) {
        if (!userMapper.exist(userId)) {
            return ResultEntity.error(StatusCode.USER_ACCOUNT_NOT_EXIST);
        }
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRole(role.name());
        return ResultEntity.data(roleMapper.addUserRole(userRole));
    }

    /**
     * 管理员添加用户权限
     *
     * @param userId 需要添加权限的用户Id
     * @param role   添加的权限
     * @return 权限添加是否成功
     */
    public ResultEntity<Boolean> removeRole(int userId, RoleEnum role) {
        if (!userMapper.exist(userId)) {
            return ResultEntity.error(StatusCode.USER_ACCOUNT_NOT_EXIST);
        }
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRole(role.name());
        return ResultEntity.data(roleMapper.removeUserRole(userRole));
    }
}