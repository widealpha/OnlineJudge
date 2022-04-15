package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.UserRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper {
    @Select("SELECT `role` FROM user_role WHERE user_id = #{userId} AND `status` = 0")
    List<String> selectRolesWithUserId(Integer userId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("REPLACE INTO user_role (user_id, `role`, `status`) VALUES (#{userId}, #{roleId}, 0)")
    boolean addUserRole(UserRole userRole);

    @Update("UPDATE user_role SET `status` = -1 WHERE user_id = #{userId} AND `role` = #{role}")
    boolean removeUserRole(UserRole userRole);
}
