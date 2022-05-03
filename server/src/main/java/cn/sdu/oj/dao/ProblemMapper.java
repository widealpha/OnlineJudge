package cn.sdu.oj.dao;

import cn.sdu.oj.controller.paramBean.problem.AddProblemParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ProblemMapper {
    @Insert("INSERT INTO problem (NAME,DESCRIPTION,EXAMPLE,TYPE,DIFFICULTY,STATUS,TIP) VALUES (#{name} ,#{description} ,#{example} ,#{type} ,#{difficulty} ,#{status} ,#{tip} )")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addProblem(AddProblemParam problem);

    @Insert("INSERT INTO problem_tag (P_ID,T_ID) VALUES (#{p_id},#{t_id})")
    int addTag(int p_id, int t_id);

    @Insert("INSERT INTO problem_answer(P_ID,ANSWER) VALUES (#{p_id},#{answer})")
    int addAnswer(int p_id, String answer);
}
