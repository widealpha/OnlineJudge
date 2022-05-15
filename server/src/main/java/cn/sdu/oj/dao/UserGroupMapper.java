package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.UserGroup;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface UserGroupMapper {

    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    @Insert("INSERT INTO `user_group` (name,type,introduction,father_id,creator_id) VALUES(#{name},#{type},#{introduction},#{fatherId},#{creatorId})")
    boolean createUserGroup(UserGroup userGroup);

    @Select("SELECT * FROM user_group WHERE id=#{id}")  //查询用户组
    UserGroup getUserGroupInfoById(Integer id);

    @Delete("UPDATE user_group SET status=-1 WHERE id=#{id}")  //删除用户组，不包含成员
    void deleteUserGroup(Integer id);

    @Delete("UPDATE user_group_user SET status=-1 WHERE user_group_id=#{id}")  //删除用户组所有成员
    void deleteAllUserGroupMember(Integer id);

    @Delete("UPDATE user_group_user SET status=-1 WHERE user_group_id=#{id} AND user_id = #{user_id}")  //删除一个成员
    void deleteUserGroupMember(Integer id,Integer user_id);

    @Update("UPDATE user_group SET name=#{name},introduction=#{introduction} WHERE `id`=#{id}")//修改用户组名称，简介
    void alterUserGroupInfo(Integer id, String name, String introduction);

    @Select("SELECT DISTINCT * FROM user_group WHERE creator_id=#{creator} AND status = 0")
    List<UserGroup> getSelfCreatedUserGroup(Integer creator);

    //向用户组里添加人员
    @Insert("INSERT INTO `user_group_user` (user_group_id,user_id) VALUES(#{user_group_id},#{user_id})")
    boolean addMemberToUserGroup(Integer user_group_id,Integer user_id);

    //查询我加入的用户组id列表
    @Select("SELECT DISTINCT `user_group_id` FROM user_group_user WHERE user_id=#{user_id} AND status = 0")
    List<Integer> getSelfJoinedUserGroup(Integer user_id);

    //查询用户组人员
    @Select("SELECT DISTINCT `user_id` FROM user_group_user WHERE user_group_id=#{user_group_id} AND status = 0")
    List<Integer> getUserGroupMembers(Integer user_group_id);

    @Update("UPDATE user_group SET children_id=#{children} WHERE id=#{user_group_id} AND status = 0")
    void updateChildrenUserGroup(Integer user_group_id, String children);
}
