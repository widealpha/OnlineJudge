package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserInfoMapper {
    @Select("SELECT * FROM user_info WHERE user_id = #{userId} AND `status` = 0")
    UserInfo selectByUserId(Integer userId);

    @Select("SELECT user_id FROM user_info WHERE email = #{email} AND `status` = 0")
    Integer selectUserIdByEmail(String email);

    @Insert("INSERT INTO user_info (user_id, `name`, nickname, email, student_id, avatar)" +
            " VALUES (#{userId}, #{name}, #{nickname}, #{email}, #{studentId}, #{avatar})")
    boolean insertUserInfo(UserInfo userInfo);

    @Update("UPDATE user_info " +
            "SET `name` = #{name}, avatar = #{avatar}, nickname = #{nickname}, student_id = #{studentId} " +
            "WHERE user_id = #{userId}")
    boolean updateUserInfo(UserInfo userInfo);

    @Select("SELECT user_id FROM user_info WHERE name LIKE '%'#{name}'%' AND `status` >= 0")
    List<UserInfo> selectUserByName(String name);

    @Select("SELECT user_id FROM user_info WHERE nickname LIKE '%'#{nickname}'%' AND `status` >= 0")
    List<UserInfo> selectUserByNickname(String nickname);
    @Select("SELECT user_id FROM user_info WHERE email LIKE '%'#{email}'%' AND `status` >= 0")
    List<UserInfo> selectUserByEmail(String email);
}
