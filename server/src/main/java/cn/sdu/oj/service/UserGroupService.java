package cn.sdu.oj.service;

import cn.sdu.oj.dao.UsergroupMapper;
import cn.sdu.oj.domain.po.Usergroup;
import cn.sdu.oj.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserGroupService {
    @Autowired
    private UsergroupMapper usergroupMapper;

    //新建用户组 添加子用户组 method1
    public StatusCode createUsergroup(String name, String type, String introduce, Integer fatherId, Integer creatorId) {
        if (usergroupMapper.createUsergroup(name, type, introduce, fatherId, creatorId)) {
            return StatusCode.SUCCESS;
        } else return StatusCode.COMMON_FAIL;

    }

    public Usergroup getUsergroupById(Integer id) {
        return usergroupMapper.getUsergroupById(id);
    }

    public StatusCode alterUsergroupInfo(Integer creator, Integer id, String name, String introduce) {

        if (usergroupMapper.getUsergroupById(id).getCreatorId().equals(creator)) {
            usergroupMapper.alterUsergroupInfo(id, name, introduce);
            return StatusCode.SUCCESS;
        } else return StatusCode.NO_PERMISSION;
    }

    public StatusCode deleteUsergroup(Integer creator, Integer id) {
        if (usergroupMapper.getUsergroupById(id).getCreatorId().equals(creator)) {
            usergroupMapper.deleteUsergroup(id);
            usergroupMapper.deleteAllUsergroupMember(id);
            return StatusCode.SUCCESS;
        } else return StatusCode.NO_PERMISSION;
    }

    public List<Usergroup> getSelfCreatedUsergroup(Integer creator) {
        return usergroupMapper.getSelfCreatedUsergroup(creator);
    }

    //向用户组里加人(只能向我创建的用户组里加人)   TODO 判重

    public StatusCode addMemberToUsergroup(Integer creator, Integer id, List<Integer> members) {
        if (usergroupMapper.getUsergroupById(id).getCreatorId().equals(creator)) {
            for (Integer onePersonId : members) {
               if( !usergroupMapper.addMemberToUsergroup(id,onePersonId)){
                   return StatusCode.COMMON_FAIL;
               }
            }
            return StatusCode.SUCCESS;
        } else
            return StatusCode.NO_PERMISSION;
    }

    //从用户组里删除人（只能从我创建的用户组里删除）
    public StatusCode deleteUsergroupMember(Integer creator,Integer usergroup_id,List<Integer> members){
        if (usergroupMapper.getUsergroupById(usergroup_id).getCreatorId().equals(creator)){
            for (Integer a:members) {
                usergroupMapper.deleteUsergroupMember(usergroup_id,a);
            }
            return StatusCode.SUCCESS;
        }else
            return StatusCode.NO_PERMISSION;
    }

    //为用户组添加子用户组 method2（复制现有用户组作为子用户组）
    public StatusCode copyUsergroupAsChildren(){
        return StatusCode.NO_PERMISSION;
    }

    //查询一个用户组的所有人，目前只能查询最底层用户组
    public List<Integer> getUsergroupMembers(Integer id){
        return usergroupMapper.getUsergroupMembers(id);
    }

    //查询我加入的所有用户组
    public List<Integer> getSelfJoinedUsergroup(Integer user_id){
        return usergroupMapper.getSelfJoinedUsergroup(user_id);
    }


}
