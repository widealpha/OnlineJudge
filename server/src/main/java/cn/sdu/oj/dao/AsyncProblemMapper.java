package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.AsyncProblem;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AsyncProblemMapper {

    @Select("SELECT * FROM async_problem WHERE id = #{id} AND status > 0")
    AsyncProblem selectProblem(int id);
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO async_problem (name, description, example, difficulty, creator, support_languages) " +
            "VALUES (#{name}, #{description}, #{example}, #{difficulty}, #{creator}, #{supportLanguages})")
    boolean insertProblem(AsyncProblem problem);

    @Update("UPDATE async_problem " +
            "SET name = #{name}, description = #{description}, example = #{example}, difficulty = #{difficulty}, support_languages = #{supportLanguages} " +
            "WHERE id = #{id}")
    boolean updateProblem(AsyncProblem problem);

    @Update("UPDATE async_problem " +
            "SET memory_limit = #{memoryLimit}, time_limit = #{timeLimit}, code_length_limit = #{codeLengthLimit} " +
            "WHERE id = #{id}")
    boolean updateProblemLimit(AsyncProblem problem);

    @Update("UPDATE async_problem SET exist_checkpoints = #{existCheckpoints} WHERE id = #{id}")
    boolean updateExistCheckpoints(int id, boolean existCheckpoints);

    @Update("UPDATE async_problem SET status = #{status} WHERE id = #{id}")
    boolean updateProblemStatus(int problemId, int status);

    @Update("UPDATE async_problem SET status = -status WHERE id = #{id} AND status > 0")
    boolean deleteProblem(Integer id);

    @Update("UPDATE async_problem SET status = -status WHERE id = #{id} AND status < 0")
    boolean recoverProblem(Integer id);
}
