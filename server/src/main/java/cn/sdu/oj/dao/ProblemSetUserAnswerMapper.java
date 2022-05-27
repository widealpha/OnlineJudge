package cn.sdu.oj.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProblemSetUserAnswerMapper {

    @Select("SELECT COUNT(*) FROM problem_set_user_answer WHERE user_id=#{user_id} AND problem_set_id=#{problem_set_id} AND status>=0")
    Integer judgeProblemSetUserAnswerExist(Integer user_id, Integer problem_set_id);

    @Insert("INSERT INTO problem_set_user_answer (user_id,problem_set_id,answer) VALUES(#{user_id},#{problem_set_id},#{answer})")
    void insertProblemSetUserAnswer(Integer user_id, Integer problem_set_id, String answer);

    @Update("UPDATE problem_set_user_answer " +
            "SET answer=#{answer}   " +
            "WHERE `status`>=0 AND `user_id`=#{user_id} AND `problem_set_id`=#{problem_set_id}  ")
    void updateProblemSetUserAnswer(Integer user_id, Integer problem_set_id, String answer);

    @Select("SELECT answer FROM problem_set_user_answer WHERE user_id=#{user_id} AND problem_set_id=#{problem_set_id} AND status>=0")
    String getProblemSetUserAnswer(Integer user_id, Integer problem_set_id);
}
