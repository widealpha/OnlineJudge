package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.AnswerRecord;
import cn.sdu.oj.domain.po.ProblemSetProblem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnswerRecordMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO answer_record (problem_id, user_id, user_answer, problem_set_id, type, is_correct, score) VALUES " +
            "(#{problemId}, #{userId}, #{userAnswer}, #{problemSetId}, #{type}, #{isCorrect}, #{score})")
    boolean insertAnswerRecord(AnswerRecord answerRecord);

    @Select("SELECT id FROM answer_record WHERE problem_id = #{problemId} AND problem_set_id = #{problemSetId} AND user_id = #{userId} AND `status` >= 0 LIMIT 1")
    Integer selectRecordByRelationIds(AnswerRecord answerRecord);

    @Select("SELECT * FROM answer_record WHERE id = #{id} AND `status` >= 0")
    AnswerRecord selectAnswerRecord(int id);

    @Select("SELECT * FROM answer_record WHERE problem_id = #{problemId} AND problem_set_id = #{problemSetId} AND type = #{type} AND `status` >= 0")
    List<AnswerRecord> selectRecordByTypeAndRelationIds(int type, int problemId, int problemSetId);

    @Select("SELECT DISTINCT problem_set_id " +
            "FROM answer_record " +
            "WHERE user_id = #{userId}  AND `status` >= 0")
    List<Integer> selectSelfDoneProblemSetByUserId(Integer userId);

    @Select("SELECT * " +
            "FROM answer_record " +
            "WHERE user_id = #{user_id} " +
            "AND problem_id = #{problem_id} " +
            "AND problem_set_id = #{problem_set_id}  " +
            "AND `status` >= 0 " +
            "ORDER BY create_time DESC LIMIT 1")
    AnswerRecord getSelfCompletion(Integer problem_id, Integer problem_set_id, Integer user_id);

    @Update("UPDATE answer_record SET score=#{score} WHERE id = #{id}")
    boolean updateScoreByPrimaryKey(int id, int score);

    @Select("SELECT COUNT(*) " +
            "FROM  answer_record " +
            "WHERE problem_id = #{problem_id} " +
            "AND `status` >= 0")
    Integer getProblemRecordNum(int problem_id);

    @Select("SELECT COUNT(*) " +
            "FROM  answer_record " +
            "WHERE problem_id = #{problem_id} " +
            "AND `status` >= 0 " +
            "AND problem_set_id = #{problem_set_id}")
    Integer getProblemRecordNum(int problem_id,int problem_set_id);

    @Select("SELECT COUNT(*) " +
            "FROM  answer_record " +
            "WHERE problem_id = #{problem_id} " +
            "AND `status` >= 0 " +
            "AND is_correct=1")
    Integer getProblemRecordCorrectNum(int problem_id);

    @Select("SELECT COUNT(*) " +
            "FROM  answer_record " +
            "WHERE problem_id = #{problem_id} " +
            "AND `status` >= 0 " +
            "AND is_correct=1 " +
            "AND problem_set_id = #{problem_set_id}")
    Integer getProblemRecordCorrectNum(int problem_id,int problem_set_id);
}
