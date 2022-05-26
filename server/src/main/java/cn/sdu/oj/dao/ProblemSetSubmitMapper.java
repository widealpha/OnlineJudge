package cn.sdu.oj.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProblemSetSubmitMapper {

    Integer judgeProblemSetSubmit(Integer user_id, Integer problem_set_id);
}
