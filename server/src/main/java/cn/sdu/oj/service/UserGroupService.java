package cn.sdu.oj.service;

import cn.sdu.oj.dao.UserGroupMapper;
import cn.sdu.oj.dao.UserMapper;
import cn.sdu.oj.domain.bo.StudentExcelInfo;
import cn.sdu.oj.domain.po.UserGroup;
import cn.sdu.oj.domain.vo.User;
import cn.sdu.oj.entity.ResultEntity;
import cn.sdu.oj.entity.StatusCode;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


@Service
public class UserGroupService {
    @Autowired
    private UserGroupMapper UserGroupMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserGroupService userGroupService;

    //新建用户组 || 添加子用户组
    public Integer createUserGroup(String name, String type, String introduction, Integer fatherId, Integer creatorId,Integer isPublic) {
        UserGroup userGroup = new UserGroup();
        userGroup.setName(name);
        userGroup.setType(type);
        userGroup.setintroduction(introduction);
        userGroup.setFatherId(fatherId);
        userGroup.setCreatorId(creatorId);
        userGroup.setIsPublic(isPublic);
        if (UserGroupMapper.createUserGroup(userGroup)) {
            return userGroup.getId();
        } else return null;

    }

    public UserGroup getUserGroupInfoById(Integer id) {
        return UserGroupMapper.getUserGroupInfoById(id);
    }

    public StatusCode alterUserGroupInfo(Integer creator, Integer id, String name, String introduction) {

        if (UserGroupMapper.getUserGroupInfoById(id).getCreatorId().equals(creator)) {
            UserGroupMapper.alterUserGroupInfo(id, name, introduction);
            return StatusCode.SUCCESS;
        } else return StatusCode.NO_PERMISSION;
    }

    public StatusCode deleteUserGroup(Integer creator, Integer id) {
        if (UserGroupMapper.getUserGroupInfoById(id).getCreatorId().equals(creator)) {
            UserGroupMapper.deleteUserGroup(id);
            UserGroupMapper.deleteAllUserGroupMember(id);
            return StatusCode.SUCCESS;
        } else return StatusCode.NO_PERMISSION;
    }

    public List<UserGroup> getSelfCreatedUserGroup(Integer creator) {
        return UserGroupMapper.getSelfCreatedUserGroup(creator);
    }

    //向用户组里加人(只能向我创建的用户组里加人)

    public StatusCode addMemberToUserGroup(Integer creator, Integer user_group_id, JSONArray members) {
        if (UserGroupMapper.getUserGroupInfoById(user_group_id).getCreatorId().equals(creator)) {
            for (int i = 0; i < members.size(); i++) {
                int onePersonId = members.getInteger(i);

                if (!judgeUserGroupContainUser(onePersonId, user_group_id)) {
                    if (!UserGroupMapper.addMemberToUserGroup(user_group_id, onePersonId)) {
                        return StatusCode.COMMON_FAIL;
                    }
                } else continue;
            }
            return StatusCode.SUCCESS;
        } else
            return StatusCode.NO_PERMISSION;
    }

    //从用户组里删除人（只能从我创建的用户组里删除）
    public StatusCode deleteUserGroupMember(Integer creator, Integer UserGroup_id, JSONArray members) {
        if (UserGroupMapper.getUserGroupInfoById(UserGroup_id).getCreatorId().equals(creator)) {
            for (int i = 0; i < members.size(); i++) {
                int onePersonId = members.getInteger(i);
                UserGroupMapper.deleteUserGroupMember(UserGroup_id, onePersonId);
            }
            return StatusCode.SUCCESS;
        } else
            return StatusCode.NO_PERMISSION;
    }

    //查询一个用户组的所有人，目前只能查询最底层用户组
    public List<Integer> getUserGroupMembers(Integer id) {
        return UserGroupMapper.getUserGroupMembers(id);
    }

    //查询我加入的所有用户组
    public List<Integer> getSelfJoinedUserGroup(Integer user_id) {
        return UserGroupMapper.getSelfJoinedUserGroup(user_id);
    }

    public void updateChildrenUserGroup(Integer fatherId, Integer id) {
        String children = UserGroupMapper.getUserGroupInfoById(fatherId).getChildrenId();

        JSONArray j = new JSONArray();
        if (children != null) {
            j = JSONArray.parseArray(children);
        }
        if (!j.contains(id)) {
            j.add(id);
        }
        UserGroupMapper.updateChildrenUserGroup(fatherId, j.toString());
    }

    public List<Integer> getUserGroupProblemSet(Integer id) {
        return UserGroupMapper.getUserGroupProblemSet(id);
    }

    public boolean linkUserGroupProblemSet(Integer user_group_id, Integer problem_set_id) {
        return UserGroupMapper.addUserGroupProblemSet(user_group_id, problem_set_id);
    }

    public boolean deleteUserGroupProblemSet(Integer user_group_id, Integer problem_set_id) {
        return UserGroupMapper.deleteUserGroupProblemSet(user_group_id, problem_set_id);
    }

    //判断用户组里是否有这个人
    public boolean judgeUserGroupContainUser(Integer user_id, Integer user_group_id) {
        List<Integer> userGroupMembers = UserGroupMapper.getUserGroupMembers(user_group_id);
        if (userGroupMembers.contains(user_id)) {
            return true;
        } else return false;
    }

    public ResultEntity<Boolean> importStudentGroup(List<StudentExcelInfo> infos, Integer creator) {
        //拿出所有班级
        HashMap all_class = new HashMap();
        Integer id = null;
        for (StudentExcelInfo info : infos) {

            String username = "sdu-" + info.getStudentId();
            User user = new User(username, passwordEncoder.encode("123456"));
            //注册成功

            if (userMapper.insert(user)) {
                int newUserId = user.getId();
                String user_group_name = info.getClassName();

                if (all_class.get(user_group_name) == null) {
                    id = userGroupService.createUserGroup(user_group_name, "班级", null, 20, creator,1);
                    all_class.put(user_group_name, id);
                    userGroupService.updateChildrenUserGroup(20,id);
                }
                UserGroupMapper.addMemberToUserGroup(id, newUserId);
            }
        }

        return ResultEntity.data(true);
    }
}
