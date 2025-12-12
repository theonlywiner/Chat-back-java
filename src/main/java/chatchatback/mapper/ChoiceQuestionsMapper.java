package chatchatback.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 选择题Mapper接口
 * 用于查询选择题题目信息
 */
@Mapper
public interface ChoiceQuestionsMapper {

    /**
     * 根据text_id查询该文本对应的所有选择题
     * @param textId 文本ID（int类型）
     * @return 选择题列表，包含question_id、question_content、options_json、correct_answer、answer_analysis等字段
     */
    @Select("SELECT question_id, question_content, options_json, correct_answer, answer_analysis FROM choice_questions WHERE text_id = #{textId}")
    List<Map<String, Object>> getQuestionsByTextId(int textId);
}