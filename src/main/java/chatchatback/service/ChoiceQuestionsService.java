package chatchatback.service;

import chatchatback.pojo.dto.PageQuery;
import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.Result;
import chatchatback.pojo.vo.ChoiceQuestionTitle;

import java.util.Map;

/**
 * 选择题服务接口
 * 提供根据text_id获取文本和选择题信息的功能
 */
public interface ChoiceQuestionsService {

    /**
     * 根据text_id获取文本信息和对应的选择题列表
     * @param textId 文本ID（int类型，支持最多约400条文本数据）
     * @return 包含text_info和questions的Map对象
     */
    Map<String, Object> getChoiceQuestionsByTextId(int textId);

    /**
     * 获取选择题文本列表，支持分页
     */
    PageResult<ChoiceQuestionTitle> getChoiceTextList(PageQuery pageQuery);
}