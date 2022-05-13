package cn.sdu.oj.dao;

import cn.sdu.oj.controller.paramBean.problem.AddProblemParam;
import cn.sdu.oj.domain.po.NonProgramProblem;
import cn.sdu.oj.domain.po.ProblemLimit;
import cn.sdu.oj.domain.po.ProgramProblem;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProblemMapper {
    @Insert("INSERT INTO program_problem (NAME,DESCRIPTION,EXAMPLE,DIFFICULTY,IS_OPEN,TIP,AUTHOR) VALUES (#{name} ,#{description} ,#{example}  ,#{difficulty} ,#{isOpen}  ,#{tip} ,#{author} )")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addProgramProblem(ProgramProblem problem);

    @Insert("INSERT INTO non_program_problem (NAME,DESCRIPTION,EXAMPLE,DIFFICULTY,IS_OPEN,TIP,AUTHOR,ANSWER) VALUES (#{name} ,#{description} ,#{example}  ,#{difficulty} ,#{isOpen}  ,#{tip} ,#{author},#{answer}  )")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addNonProgramProblem(NonProgramProblem problem);

    @Insert("INSERT INTO problem_tag (P_ID,T_ID,TYPE) VALUES (#{p_id},#{t_id},#{type})")
    int addTag(int p_id, int t_id, int type);

    @Update("UPDATE problem_tag SET STATUS = 1 WHERE P_ID=#{p_id}  AND TYPE=#{type}")
    int deleteTag(int p_id, int type);

    @Insert("INSERT INTO problem_limit (PROBLEM_ID,TIME,MEMORY,TEXT) VALUES (#{problemId} ,#{time} ,#{memory} ,#{text} )")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addProblemLimit(ProblemLimit limit);

    @Select("SELECT PROBLEM_ID,TIME,MEMORY,TEXT FROM problem_limit WHERE PROBLEM_ID=#{problemId} AND STATUS=0")
    ProblemLimit getProblemLimitByProblemId(int problemId);


    @Update("UPDATE program_problem SET IS_DELETE=1 WHERE AUTHOR=#{u_id} AND ID=#{p_id}")
    int deleteProgramProblem(int u_id, int p_id);

    @Update("UPDATE non_program_problem SET IS_DELETE=1 WHERE AUTHOR=#{u_id} AND ID=#{p_id}")
    int deleteNonProgramProblem(int u_id, int p_id);

    @Update("UPDATE program_problem SET NAME=#{name} ,DESCRIPTION=#{description} ,EXAMPLE=#{example} ,IS_OPEN=#{isOpen} ,DIFFICULTY=#{difficulty} ,TIP=#{tip}  WHERE ID=#{id} AND AUTHOR=#{author} ")
    int updateProgramProblem(ProgramProblem problem);

    @Update("UPDATE non_program_problem SET NAME=#{name} ,DESCRIPTION=#{description} ,EXAMPLE=#{example} ,IS_OPEN=#{isOpen} ,DIFFICULTY=#{difficulty} ,TIP=#{tip},ANSWER=#{answer}   WHERE ID=#{id} AND AUTHOR=#{author} ")
    int updateNonProgramProblem(NonProgramProblem problem);

    @Select("SELECT 1 FROM program_problem WHERE ID=#{problemId} AND STATUS=0")
    Integer isProblemExist(int problemId);
}
