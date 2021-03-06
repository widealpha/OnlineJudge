package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.ProblemSetProblem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProblemSetProblemMapper {

    @Select("SELECT * FROM problem_set_problem WHERE problem_set_id=#{problem_set_id} AND status >= 0")
    List<ProblemSetProblem> getProblemSetProblem(Integer problem_set_id);

    @Select("SELECT COUNT(1) FROM problem_set_problem WHERE problem_set_id=#{problem_set_id} AND problem_id=#{problem_id} AND status >= 0")
    boolean existProblemSetProblem(Integer problem_set_id, Integer problem_id);

    @Select("SELECT * FROM problem_set_problem WHERE problem_set_id=#{problem_set_id} AND problem_id=#{problem_id} AND status >= 0")
    ProblemSetProblem getProblemSetProblemScore(Integer problem_set_id,Integer problem_id);


    @Insert("INSERT INTO problem_set_problem (problem_id,problem_set_id) VALUES(#{problem_id},#{problem_set_id})")
    void addProblemToProblemSet(Integer problem_id, Integer problem_set_id);

    @Select("SELECT score FROM problem_set_problem WHERE problem_set_id=#{problem_set_id} AND problem_id=#{problem_id} AND status >= 0")
    Integer selectScoreByProblemSetAndProblemId(int problemSetId, int problemId);
}
