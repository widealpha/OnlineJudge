package cn.sdu.oj.dao;

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

    @Select("SELECT DISTINCT problem_set_id " +
            "FROM answer_record " +
            "WHERE user_id = #{userId}  AND `status` >= 0")
    List<Integer> selectSelfDoneProblemSetByUserId(Integer userId);

    @Select("SELECT is_correct FROM answer_record WHERE user_id = #{userId} AND problem_id = #{problem_id} AND problem_set_id = #{problem_set_id}  AND `status` >= 0")
    Integer getSelfCompletion(Integer problem_id, Integer problem_set_id, ProblemSetProblem p, Integer user_id);
}
