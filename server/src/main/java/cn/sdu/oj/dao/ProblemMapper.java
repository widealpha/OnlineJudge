package cn.sdu.oj.dao;

import cn.sdu.oj.controller.paramBean.problem.AddProblemParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProblemMapper {
    @Insert("INSERT INTO problem (NAME,DESCRIPTION,EXAMPLE,TYPE,DIFFICULTY,STATUS,TIP,AUTHOR) VALUES (#{name} ,#{description} ,#{example} ,#{type} ,#{difficulty} ,#{status} ,#{tip} ,#{author} )")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addProblem(AddProblemParam problem);

    @Insert("INSERT INTO problem_tag (P_ID,T_ID) VALUES (#{p_id},#{t_id})")
    int addTag(int p_id, int t_id);

    @Insert("INSERT INTO problem_answer(P_ID,ANSWER) VALUES (#{p_id},#{answer})")
    int addAnswer(int p_id, String answer);

    @Update("UPDATE problem SET IS_DELETE=1 WHERE AUTHOR=#{u_id} AND ID=#{p_id}")
    int deleteProblem(int u_id, int p_id);

    @Update("UPDATE problem SET NAME=#{name} ,DESCRIPTION=#{description} ,EXAMPLE=#{example} ,TYPE=#{type} ,DIFFICULTY=#{difficulty} ,STATUS=#{status} ,TIP=#{tip} ,LAST_MODIFY_TIME=NOW() WHERE ID=#{id} AND AUTHOR=#{author} ")
    int updateProblem(AddProblemParam problem);
}
