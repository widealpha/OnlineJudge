package cn.sdu.oj.dao;

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
            "SET `memory` = #{memory}, cpu_time = #{cpuTime}, error = #{error}, `status` = #{status} " +
            "WHERE id = #{id}")
    boolean updateSolveRecordByPrimaryKey(SolveRecord record);

    @Insert("INSERT INTO solve_record(user_id, problem_id, `language`, `code`) " +
            "VALUES(#{userId}, #{problemId}, #{language}, #{code})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    boolean insertRecord(SolveRecord record);
}
