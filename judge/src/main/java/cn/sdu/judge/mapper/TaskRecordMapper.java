package cn.sdu.judge.mapper;

import cn.sdu.judge.bean.TaskRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TaskRecordMapper {
    @Select("SELECT * FROM task_record WHERE task_id = #{taskId}")
    TaskRecord selectTaskRecordByTaskId(Integer taskId);

    @Insert("INSERT INTO task_record(task_id, `language`, `code`, code_length, special_judge, special_judge_language) " +
            "VALUES (#{taskId}, #{language}, #{code}, #{codeLength},#{specialJudge}, #{specialJudgeLanguage})")
    long insertTaskRecord(TaskRecord taskRecord);

    @Select("SELECT COUNT(*) FROM task_record WHERE task_id = #{taskId}")
    boolean existTaskRecord(String taskId);

    @Update("UPDATE task_record SET `language` = #{language}, `code` = #{code}, code_length = #{codeLength}, " +
            "special_judge = #{specialJudge}, special_judge_language = #{specialJudgeLanguage}, " +
            "compile_info = #{compileInfo}, run_info = #{runInfo} " +
            "WHERE task_id = #{taskId}")
    boolean updateTaskRecord(TaskRecord taskRecord);

    @Update("UPDATE task_record SET status = #{status} WHERE task_id = #{taskId}")
    boolean updateTaskRecordStatus(String taskId, int status);
}
