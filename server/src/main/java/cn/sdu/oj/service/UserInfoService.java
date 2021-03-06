package cn.sdu.oj.service;

import cn.sdu.oj.dao.RoleMapper;
import cn.sdu.oj.dao.UserInfoMapper;
import cn.sdu.oj.dao.UserMapper;
import cn.sdu.oj.domain.dto.MinorUserInfoDto;
import cn.sdu.oj.domain.dto.UserInfoDto;
import cn.sdu.oj.domain.po.UserInfo;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import cn.sdu.oj.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserInfoService {
    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    RoleMapper roleMapper;

    @Resource
    UserMapper userMapper;

    public ResultEntity<UserInfoDto> userInfo(int userId) {
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo == null) {
            return ResultEntity.error(StatusCode.USER_ACCOUNT_NOT_EXIST);
        } else {
            UserInfoDto userInfoDto = new UserInfoDto(userInfo);
            userInfoDto.setUsername(userMapper.selectUsernameById(userId));
            List<String> roles = roleMapper.selectRolesByUserId(userId);
            //添加默认身份
            roles.add("ROLE_COMMON");
            userInfoDto.setRoles(roles);
            return ResultEntity.data(userInfoDto);
        }
    }

    public ResultEntity<MinorUserInfoDto> minorUserInfo(int userId) {
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo == null) {
            return ResultEntity.error(StatusCode.USER_ACCOUNT_NOT_EXIST);
        } else {
            MinorUserInfoDto userInfoDto = new MinorUserInfoDto();
            userInfoDto.setUserId(userId);
            userInfoDto.setAvatar(userInfo.getAvatar());
            userInfoDto.setUsername(userMapper.selectUsernameById(userId));
            userInfoDto.setNickname(userInfo.getNickname());
            return ResultEntity.data(userInfoDto);
        }
    }

    public ResultEntity<Boolean> updateUserInfo(UserInfo userInfo) {
        UserInfo info = userInfoMapper.selectByUserId(userInfo.getUserId());
        if (info == null) {
            return ResultEntity.error(StatusCode.USER_ACCOUNT_NOT_EXIST);
        } else {
            info.setName(userInfo.getName());
            info.setAvatar(userInfo.getAvatar());
            info.setNickname(userInfo.getNickname());
            return ResultEntity.data(userInfoMapper.updateUserInfo(info));

        }
    }

    public ResultEntity<List<MinorUserInfoDto>> searchUserByUsername(String username) {
        List<Integer> userIds = userMapper.selectUsernameLike(username);
        List<MinorUserInfoDto> infos = new ArrayList<>();
        for (Integer userId : userIds) {
            infos.add(minorUserInfo(userId).getData());
        }
        return ResultEntity.data(infos);
    }

    public ResultEntity<List<MinorUserInfoDto>> searchUserByNickname(String nickname) {
        List<UserInfo> allInfos = userInfoMapper.selectUserByNickname(nickname);
        List<MinorUserInfoDto> infos = new ArrayList<>();
        for (UserInfo info : allInfos) {
            infos.add(new MinorUserInfoDto(info, userMapper.selectUsernameById(info.getUserId())));
        }
        return ResultEntity.data(infos);
    }
}
