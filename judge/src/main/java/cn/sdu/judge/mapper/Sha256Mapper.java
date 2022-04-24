package cn.sdu.judge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface Sha256Mapper {
    @Select("SELECT * FROM sha WHERE problemId = #{problemId}")
    String checkpointZipSha256(int problemId);

    @Update("UPDATE sha SET checkpoints = #{sha256} WHERE problemId = #{problemId} ")
    boolean updateCheckpointZipSha256(int problemId, String sha256);

    @Select("SELECT * FROM sha WHERE problemId = #{problemId}")
    String specialJudgeSha256(int problemId);

    @Update("UPDATE sha SET special_judge = #{sha256} WHERE problemId = #{problemId} ")
    boolean updateSpecialJudgeSha256(int problemId, String sha256);
}
