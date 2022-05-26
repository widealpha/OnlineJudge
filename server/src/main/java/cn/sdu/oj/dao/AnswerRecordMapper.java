package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.AnswerRecord;
import cn.sdu.oj.domain.po.ProblemSet;
import cn.sdu.oj.domain.po.ProblemSetProblem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;


import cn.sdu.oj.domain.po.ProblemSet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnswerRecordMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO answer_record (problem_id, user_id, user_answer, problem_set_id, type, is_correct) VALUES " +
            "(#{problemId}, #{userId}, #{userAnswer}, #{problemSetId}, #{type}, #{isCorrect})")
    boolean insertAnswerRecord(AnswerRecord answerRecord);

    @Select("SELECT id FROM answer_record WHERE problem_id = #{problem_id} AND problem_set_id = #{problem_set_id} AND user_id = #{user_id} AND `status` >= 0 LIMIT 1")
    Integer exist(AnswerRecord answerRecord);

    @Select("SELECT * FROM answer_record WHERE id = #{id} AND `status` >= 0")
    AnswerRecord selectAnswerRecord(int id);

    @Select("SELECT DISTINCT problem_set_id " +
            "FROM answer_record " +
            "WHERE user_id = #{userId}  AND `status` >= 0")
    List<Integer> selectSelfDoneProblemSetByUserId(Integer userId);

    @Select("SELECT is_correct FROM answer_record WHERE user_id = #{userId} AND problem_id = #{problem_id} AND problem_set_id = #{problem_set_id}  AND `status` >= 0")
    Integer getSelfCompletion(Integer problem_id, Integer problem_set_id, ProblemSetProblem p, Integer user_id);
}
