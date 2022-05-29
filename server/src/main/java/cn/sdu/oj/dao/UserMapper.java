package cn.sdu.oj.dao;

import cn.sdu.oj.domain.vo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM `user` WHERE id = #{id} AND `status` = 0")
    User selectByPrimaryKey(Integer id);

    @Update("UPDATE user SET password=#{password}, username=#{username} WHERE id = #{id}")
    boolean updateByPrimaryKey(User user);

    @Select("SELECT * FROM `user` WHERE username = #{username} AND `status` = 0")
    User selectByUsername(String username);

    @Select("SELECT id FROM `user` WHERE username LIKE '%'#{username}'%' AND `status` = 0")
    List<Integer> selectUsernameLike(String username);

    @Select("SELECT username FROM `user` WHERE id = #{userId} AND `status` = 0")
    String selectUsernameById(int userId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO `user` (username, `password`) VALUES (#{username}, #{password})")
    boolean insert(User user);

    @Select("SELECT COUNT(1) FROM `user` WHERE id = #{id} AND `status` = 0")
    boolean exist(int userId);
}
