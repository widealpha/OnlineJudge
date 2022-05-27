package cn.sdu.oj.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProblemSetSubmitMapper {

    @Select("SELECT is_submit FROM  problem_set_submit WHERE  user_id=#{user_id} AND problem_set_id =#{problem_set_id} AND status >= 0")
    Integer judgeProblemSetSubmit(Integer user_id, Integer problem_set_id);
}
