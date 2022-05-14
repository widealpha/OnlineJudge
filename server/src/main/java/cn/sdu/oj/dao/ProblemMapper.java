package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

    @Update("UPDATE problem_limit SET TIME=#{time} ,MEMORY=#{memory} ,TEXT=#{text} WHERE PROBLEM_ID=#{problemId} )")
    void updateProblemLimit(ProblemLimit limit);


    @Update("UPDATE program_problem SET IS_DELETE=1 WHERE AUTHOR=#{u_id} AND ID=#{p_id}")
    int deleteProgramProblem(int u_id, int p_id);

    @Update("UPDATE non_program_problem SET IS_DELETE=1 WHERE AUTHOR=#{u_id} AND ID=#{p_id}")
    int deleteNonProgramProblem(int u_id, int p_id);

    @Update("UPDATE program_problem SET NAME=#{name} ,DESCRIPTION=#{description} ,EXAMPLE=#{example} ,IS_OPEN=#{isOpen} ,DIFFICULTY=#{difficulty} ,TIP=#{tip}  WHERE ID=#{id} AND AUTHOR=#{author} ")
    int updateProgramProblem(ProgramProblem problem);

    @Update("UPDATE non_program_problem SET NAME=#{name} ,DESCRIPTION=#{description} ,EXAMPLE=#{example} ,IS_OPEN=#{isOpen} ,DIFFICULTY=#{difficulty} ,TIP=#{tip},ANSWER=#{answer}   WHERE ID=#{id} AND AUTHOR=#{author} ")
    int updateNonProgramProblem(NonProgramProblem problem);

    // @Select("SELECT 1 FROM program_problem WHERE ID=#{problemId} AND STATUS=0")
    // Integer isProblemExist(int problemId);

    @Select("SELECT * FROM program_problem WHERE ID=#{id} ")
    ProgramProblem getProgramProblemById(int id);

    @Select("SELECT * FROM non_program_problem WHERE ID=#{id} ")
    NonProgramProblem getNonProgramProblemById(int id);

    @Select("SELECT * FROM tag WHERE ID IN " +
            "(SELECT  T_ID FROM problem_tag WHERE P_ID =#{problemId} AND TYPE=#{type} AND STATUS=0)")
    List<Tag> getTagListByProblemIdAndType(int problemId, int type);

    @Select("with recursive tmp as                                                                   " +
            "   ( select *                                                                                    " +
            "    from tag                                                                                 " +
            "    where id in (select t_id from problem_tag where p_id = #{problemId} and type=#{type})                                   " +
            "    union                                                                                                          " +
            "    select tmp.id, tag.parent, concat_ws('/', tag.name, tmp.name) name, tmp.level                                  " +
            "    from tmp                                                                               " +
            "             join tag on (tmp.parent = tag.id)                                                     " +
            ")                                                                                              " +
            "select                                                                                                             " +
            "tmp.id,tmp.name,tmp.level                                                                      " +
            "from tmp                                                                                           " +
            "where parent = -1                                                                                      "
    )
    List<Tag> getTagListWithPrefixByProblemIdAndType(int problemId, int type);

    @Select("SELECT * FROM problem_limit WHERE PROBLEM_ID=#{problemId} AND STATUS=0")
    ProblemLimit getProblemLimitByProblemId(int problemId);

    @Select("SELECT U.* FROM user_info U JOIN program_problem P ON (P.AUTHOR=U.USER_ID) WHERE P.ID=#{problemId}  ")
    UserInfo getAuthorNameByProgramProblemId(int problemId);

    @Select("SELECT U.* FROM user_info U JOIN non_program_problem P ON (P.AUTHOR=U.USER_ID) WHERE P.ID=#{problemId}  ")
    UserInfo getAuthorNameByNonProgramProblemId(int problemId);

    @Select("SELECT * FROM tag WHERE LEVEL=1")
    List<Tag> getTopLevelTag();

    @Select("SELECT * FROM tag WHERE PARENT=#{parentId} ")
    List<Tag> getChildrenTagByParentId(int parentId);

    @Select("SELECT COUNT(*) FROM program_problem WHERE ID=#{problemId} AND STATUS=0")
    boolean isProblemExist(int problemId);
}
