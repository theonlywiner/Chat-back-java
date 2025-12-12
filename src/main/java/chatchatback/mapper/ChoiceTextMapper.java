package chatchatback.mapper;

import chatchatback.pojo.vo.ChoiceQuestionTitle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 选择题文本Mapper接口
 * 用于查询choice_text表中的文本信息
 */
@Mapper
public interface ChoiceTextMapper {

    /**
     * 根据text_id查询文本信息
     * @param textId 文本ID（int类型）
     * @return 包含text_id、title、content的Map对象
     */
    @Select("SELECT text_id, title, content FROM choice_text WHERE text_id = #{textId}")
    Map<String, Object> getTextByTextId(int textId);

    /**
     * 查询所有文本的id和标题，支持分页
     */
    @Select("SELECT text_id, title FROM choice_text ORDER BY text_id")
    List<ChoiceQuestionTitle> getTextIdAndTitleList();
}