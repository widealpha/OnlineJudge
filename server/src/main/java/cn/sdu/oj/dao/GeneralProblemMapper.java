package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.GeneralProblem;
import cn.sdu.oj.domain.po.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GeneralProblemMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO general_problem (type, type_problem_id, difficulty, creator) " +
            "VALUES (#{type}, #{typeProblemId}, #{difficulty}, #{creator})")
    boolean insertGeneralProblem(GeneralProblem generalProblem);

    @Update("UPDATE general_problem SET status =-status WHERE id = #{id} AND status > 0")
    boolean deleteGeneralProblem(int id);

    @Update("UPDATE general_problem SET status =-status WHERE id = #{id} AND status < 0")
    boolean recoverGeneralProblem(int id);

    @Select("SELECT * FROM general_problem WHERE id = #{id} AND status > 0")
    GeneralProblem selectGeneralProblem(int id);

    @Select("SELECT type_problem_id FROM general_problem WHERE id = #{id} AND status > 0")
    Integer selectTypeProblemIdById(int problemId);

    @Insert("INSERT INTO problem_tag (problem_id, tag_id) VALUES (#{problemId}, #{tagId}) " +
            "ON DUPLICATE KEY UPDATE status = 1")
    boolean addProblemTag(int problemId, int tagId);

    @Update("UPDATE problem_tag SET status=-status WHERE problem_id = #{problemId} AND tag_id = #{tagId} AND status > 0")
    boolean deleteProblemTag(int problemId, int tagId);

    @Update("UPDATE problem_tag SET status=-status WHERE problem_id = #{problemId} AND tag_id = #{tagId} AND status < 0")
    boolean recoveryProblemTag(int problemId, int tagId);

    @Select("SELECT * FROM tag " +
            "WHERE id " +
            "IN (SELECT tag_id FROM problem_tag WHERE problem_id = #{problemId} AND status > 0)")
    List<Tag> selectProblemTags(int problemId);
}
