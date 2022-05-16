package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.ProblemSet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProblemSetMapper {
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    @Insert("INSERT INTO `problem_set` (name,type,introduction,is_public,begin_time,end_time,creator_id) VALUES(#{name},#{type},#{introduction},#{is_public},#{begin_time},#{end_time},#{creator_id})")
    boolean createProblemSet(ProblemSet problemSet);

    @Select("SELECT * FROM problem_set WHERE `status`=0 AND `is_public`=1")
    List<ProblemSet> getPublicProblemSet();
}
