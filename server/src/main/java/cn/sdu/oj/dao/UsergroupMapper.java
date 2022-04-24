package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.Usergroup;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface UsergroupMapper {

    @Insert("INSERT INTO `usergroup` (name,type,introduce,father_id,creator_id) VALUES(#{name},#{type},#{introduce},#{fatherId},#{creatorId})")
    boolean createUsergroup(String name,String type,String introduce,Integer fatherId,Integer creatorId);

    @Select("SELECT * FROM usergroup WHERE id=#{id}")  //查询用户组
    Usergroup getUsergroupById(Integer id);

    @Delete("UPDATE usergroup SET status=-1 WHERE id=#{id}")  //删除用户组，不包含成员
    void deleteUsergroup(Integer id);

    @Delete("UPDATE usergroup_user SET status=-1 WHERE usergroup_id=#{id}")  //删除用户组所有成员
    void deleteAllUsergroupMember(Integer id);

    @Delete("UPDATE usergroup_user SET status=-1 WHERE usergroup_id=#{id} AND user_id = #{user_id}")  //删除一个成员
    void deleteUsergroupMember(Integer id,Integer user_id);

    @Update("UPDATE usergroup SET name=#{name},introduce=#{introduce} WHERE `id`=#{id}")//修改用户组名称，简介
    void alterUsergroupInfo(Integer id, String name, String introduce);

    @Select("SELECT DISTINCT * FROM usergroup WHERE creator_id=#{creator} AND status = 0")
    List<Usergroup> getSelfCreatedUsergroup(Integer creator);

    //向用户组里添加人员
    @Insert("INSERT INTO `usergroup_user` (usergroup_id,user_id) VALUES(#{usergroup_id},#{user_id})")
    boolean addMemberToUsergroup(Integer usergroup_id,Integer user_id);

    //查询我加入的用户组id列表
    @Select("SELECT DISTINCT `usergroup_id` FROM usergroup_user WHERE user_id=#{user_id} AND status = 0")
    List<Integer> getSelfJoinedUsergroup(Integer user_id);

    //查询用户组人员
    @Select("SELECT DISTINCT `user_id` FROM usergroup_user WHERE usergroup_id=#{usergroup_id} AND status = 0")
    List<Integer> getUsergroupMembers(Integer usergroup_id);
}
