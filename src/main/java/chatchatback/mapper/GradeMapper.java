package chatchatback.mapper;

import chatchatback.pojo.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GradeMapper {
    /**
     * 获取年级列表
     * @return
     */
    @Select("select id,name from grades order by id")
    List<Grade> getGradeList();

    /**
     * 获取当前用户的年级id
     */
    @Select("select grade_id from user where id = #{id}")
    Integer getGradeIdById(Integer id);

    /**
     * 根据gradeId，获取当年级名称
     */
    @Select("select name from grades where id = #{gradeId}")
    String getGradeNameByGradeId(Integer gradeId);
}
