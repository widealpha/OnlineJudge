package cn.sdu.oj.service;

import cn.sdu.oj.dao.UserGroupMapper;
import cn.sdu.oj.domain.po.UserGroup;
import cn.sdu.oj.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserGroupService {
    @Autowired
    private UserGroupMapper UserGroupMapper;

    //新建用户组 添加子用户组 method1
    public StatusCode createUserGroup(String name, String type, String introduction, Integer fatherId, Integer creatorId) {
        if (UserGroupMapper.createUserGroup(name, type, introduction, fatherId, creatorId)) {
            return StatusCode.SUCCESS;
        } else return StatusCode.COMMON_FAIL;

    }

    public UserGroup getUserGroupById(Integer id) {
        return UserGroupMapper.getUserGroupById(id);
    }

    public StatusCode alterUserGroupInfo(Integer creator, Integer id, String name, String introduction) {

        if (UserGroupMapper.getUserGroupById(id).getCreatorId().equals(creator)) {
            UserGroupMapper.alterUserGroupInfo(id, name, introduction);
            return StatusCode.SUCCESS;
        } else return StatusCode.NO_PERMISSION;
    }

    public StatusCode deleteUserGroup(Integer creator, Integer id) {
        if (UserGroupMapper.getUserGroupById(id).getCreatorId().equals(creator)) {
            UserGroupMapper.deleteUserGroup(id);
            UserGroupMapper.deleteAllUserGroupMember(id);
            return StatusCode.SUCCESS;
        } else return StatusCode.NO_PERMISSION;
    }

    public List<UserGroup> getSelfCreatedUserGroup(Integer creator) {
        return UserGroupMapper.getSelfCreatedUserGroup(creator);
    }

    //向用户组里加人(只能向我创建的用户组里加人)   TODO 判重

    public StatusCode addMemberToUserGroup(Integer creator, Integer id, List<Integer> members) {
        if (UserGroupMapper.getUserGroupById(id).getCreatorId().equals(creator)) {
            for (Integer onePersonId : members) {
               if( !UserGroupMapper.addMemberToUserGroup(id,onePersonId)){
                   return StatusCode.COMMON_FAIL;
               }
            }
            return StatusCode.SUCCESS;
        } else
            return StatusCode.NO_PERMISSION;
    }

    //从用户组里删除人（只能从我创建的用户组里删除）
    public StatusCode deleteUserGroupMember(Integer creator,Integer UserGroup_id,List<Integer> members){
        if (UserGroupMapper.getUserGroupById(UserGroup_id).getCreatorId().equals(creator)){
            for (Integer a:members) {
                UserGroupMapper.deleteUserGroupMember(UserGroup_id,a);
            }
            return StatusCode.SUCCESS;
        }else
            return StatusCode.NO_PERMISSION;
    }

    //为用户组添加子用户组 method2（复制现有用户组作为子用户组）
    public StatusCode copyUserGroupAsChildren(){
        return StatusCode.NO_PERMISSION;
    }

    //查询一个用户组的所有人，目前只能查询最底层用户组
    public List<Integer> getUserGroupMembers(Integer id){
        return UserGroupMapper.getUserGroupMembers(id);
    }

    //查询我加入的所有用户组
    public List<Integer> getSelfJoinedUserGroup(Integer user_id){
        return UserGroupMapper.getSelfJoinedUserGroup(user_id);
    }


}
