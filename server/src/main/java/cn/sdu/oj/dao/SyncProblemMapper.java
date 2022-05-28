package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.SyncProblem;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SyncProblemMapper {
    @Select("SELECT * FROM sync_problem WHERE id = #{id} AND status > 0")
    SyncProblem selectProblem(int id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO sync_problem (name, type, description, difficulty, options, answer, creator) " +
            "VALUES (#{name}, #{type}, #{description}, #{difficulty}, #{options}, #{answer}, #{creator})")
    boolean insertProblem(SyncProblem problem);

    @Update("UPDATE sync_problem SET name = #{name}, type = #{type}, description = #{description}, difficulty = #{difficulty}, options = #{options}, answer = #{answer} WHERE id = #{id}")
    boolean updateProblem(SyncProblem problem);

    @Update("UPDATE sync_problem SET status=-status WHERE id = #{id} AND status > 0")
    boolean deleteProblem(int id);

    @Update("UPDATE sync_problem SET status = -status WHERE id = #{id} AND status < 0")
    boolean recoverProblem(Integer id);
}
