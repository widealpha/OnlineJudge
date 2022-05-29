package cn.sdu.oj.dao;

import cn.sdu.oj.domain.po.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TagMapper {
    @Select("SELECT distinct * FROM tag WHERE `status` > 0")
    List<Tag> selectAllTags();

    @Select("SELECT id FROM tag WHERE name = #{name}")
    Integer selectTagIdByTagName(String tagName);

    @Select("SELECT COUNT(1) FROM tag WHERE id=#{id} AND `status`>0")
    boolean exist(int tagId);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO tag (`name`, `level`) VALUES (#{name}, 1)")
    boolean insertTag(Tag tag);

    @Update("UPDATE tag SET `status`=-`status` WHERE id=#{id} AND status>0")
    boolean deleteTag(int tagId);

    @Update("UPDATE tag SET `status`=-`status` WHERE id=#{id} AND status<0")
    boolean recoverTag(int tagId);

    @Select("SELECT * FROM tag WHERE PARENT=#{parentId} ")
    List<Tag> getChildrenTagByParentId(int parentId);

    @Select("SELECT * FROM tag WHERE LEVEL=1")
    List<Tag> getTopLevelTag();

}
