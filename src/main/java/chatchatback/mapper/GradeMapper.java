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
}
