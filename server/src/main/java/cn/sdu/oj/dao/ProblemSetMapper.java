package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.ProblemSet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProblemSetMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO `problem_set` (name,type,introduction,is_public,begin_time,end_time,creator_id) VALUES(#{name},#{type},#{introduction},#{isPublic},#{beginTime},#{endTime},#{creatorId})")
    boolean createProblemSet(ProblemSet problemSet);

    @Select("SELECT * FROM problem_set WHERE `status`=0 AND `is_public`=1")
    List<ProblemSet> getPublicProblemSet();

    @Select("SELECT * FROM problem_set WHERE `status`=0 AND `id`=#{id}")
    ProblemSet getProblemSetInfo(Integer id);

    @Select("SELECT * FROM problem_set WHERE `status`=0 AND `creator_id`=#{creator_id}")
    List<ProblemSet> getSelfCreatedProblemSet(Integer creator_id);

    @Update("UPDATE problem_set " +
            "SET name=#{name} ,introduction=#{introduction} ,is_public=#{isPublic},begin_time=#{beginTime},end_time=#{endTime} " +
            "WHERE `status`=0 AND `id`=#{id}")
    boolean alterProblemSetInfo(ProblemSet problemSet);

    @Update("UPDATE problem_set " +
            "SET status=#{name} " +
            "WHERE `id`=#{problemSetId} ")
    void deleteProblemSet(Integer problemSetId);

    @Select("SELECT * FROM problem_set WHERE `status`=0 AND `is_public`=1 AND name LIKE #{name}"  )
    List<ProblemSet> selectPublicProblemSetByName(String name);
}
