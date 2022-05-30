package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.ProblemSetProblem;
import cn.sdu.oj.domain.po.SolveRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SolveRecordMapper {
    @Select("SELECT * " +
            "FROM solve_record " +
            "WHERE user_id = #{userId} AND problem_id = #{problemId} AND `status` > 0")
    List<SolveRecord> selectSolveRecord(Integer problemId, Integer userId);

    @Select("SELECT * " +
            "FROM solve_record " +
            "WHERE id = #{id}")
    SolveRecord selectSolveRecordByPrimaryKey(int id);

    /**
     * 正在判别的未完成的题目数量
     *
     * @param userId 用户id
     */
    @Select("SELECT COUNT(*) " +
            "FROM solve_record " +
            "WHERE user_id = #{userId} AND `status` = 0")
    int unfinishedSolveCount(int userId);

    /**
     * 用户关于某题目最后提交的代码
     *
     * @param problemId 题目编号
     * @param userId    用户Id
     */
    @Select("SELECT code " +
            "FROM solve_record " +
            "WHERE user_id = #{userId} AND problem_id = #{problemId} " +
            "ORDER BY id DESC LIMIT 1")
    String problemLatestModifiedCode(int problemId, int userId);

    /**
     * 更新提交的记录
     *
     * @param record 题解记录
     */
    @Update("UPDATE solve_record " +
            "SET `memory` = #{memory}, cpu_time = #{cpuTime}, " +
            "checkpoint_size = #{checkpointSize}, total_correct = #{totalCorrect}, error = #{error}, `status` = #{status} " +
            "WHERE id = #{id}")
    boolean updateSolveRecordByPrimaryKey(SolveRecord record);

    @Insert("INSERT INTO solve_record(user_id, problem_id, `language`, `code`, problem_set_id) " +
            "VALUES(#{userId}, #{problemId}, #{language}, #{code}, #{problemSetId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    boolean insertRecord(SolveRecord record);

    @Select("SELECT DISTINCT problem_set_id " +
            "FROM solve_record " +
            "WHERE user_id = #{userId}  AND `status` >= 0")
    List<Integer> selectSelfDoneProblemSetByUserId(Integer userId);

    @Select("SELECT *" +
            "FROM solve_record " +
            "WHERE problem_id = #{problem_id} AND problem_set_id = #{problem_set_id} AND user_id = #{user_id} " +
            "ORDER BY create_time DESC LIMIT 1")
    SolveRecord getSelfCompletion(Integer problem_id, Integer problem_set_id,  Integer user_id);

//    @Select("SELECT COUNT(*) " +
//            "FROM  solve_record WHERE problem_id = #{problem_id} AND `status` >= 0")
//    Integer getProblemRecordNum(int problem_id);

    @Select("SELECT COUNT(*) " +
            "FROM  solve_record WHERE problem_id = #{problem_id} AND `status` >= 0 AND problem_set_id = #{problem_set_id}")
    Integer getProblemRecordNum(int problem_id,int problem_set_id);
//
//    @Select("SELECT COUNT(*) " +
//            "FROM  solve_record WHERE problem_id = #{problem_id} AND `status` = 1")
//    Integer getProblemRecordCorrectNum(int problem_id);

    @Select("SELECT COUNT(*) " +
            "FROM  solve_record WHERE problem_id = #{problem_id} AND `status` = 1 AND problem_set_id = #{problem_set_id}")
    Integer getProblemRecordCorrectNum(int problem_id,int problem_set_id);
}
