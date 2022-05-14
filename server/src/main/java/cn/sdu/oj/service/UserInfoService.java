package cn.sdu.oj.service;

import cn.sdu.oj.dao.UserInfoMapper;
import cn.sdu.oj.domain.po.UserInfo;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserInfoService {
    @Resource
    UserInfoMapper userInfoMapper;

    public ResultEntity<UserInfo> userInfo(int userId) {
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo == null) {
            return ResultEntity.error(StatusCode.USER_ACCOUNT_NOT_EXIST);
        } else {
            return ResultEntity.data(userInfo);
        }
    }
}
