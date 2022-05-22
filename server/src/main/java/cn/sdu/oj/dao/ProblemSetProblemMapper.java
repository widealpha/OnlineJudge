package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.ProblemSetProblem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProblemSetProblemMapper {

    @Select("SELECT * FROM problem_set_problem WHERE problem_set_id=#{problem_set_id} AND status >= 0")
    List<ProblemSetProblem> getProblemSetProblem(Integer problem_set_id);
}
