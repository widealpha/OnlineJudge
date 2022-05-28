package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.ProblemSetUserGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProblemSetUserGroupMapper {

    @Select("SELECT DISTINCT * FROM user_group_problem_set WHERE problem_set_id=#{problem_set_id} AND status>=0")
    List<ProblemSetUserGroup> getUserGroupByProblemSet(Integer problem_set_id);
}
