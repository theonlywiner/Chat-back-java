package chatchatback.service.impl;

import chatchatback.mapper.ChoiceQuestionsMapper;
import chatchatback.mapper.ChoiceTextMapper;
import chatchatback.pojo.dto.PageQuery;
import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.Result;
import chatchatback.pojo.vo.ChoiceQuestionTitle;
import chatchatback.service.ChoiceQuestionsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 选择题服务实现类
 * 实现根据text_id获取文本和选择题信息的功能
 */
@Service
public class ChoiceQuestionsServiceImpl implements ChoiceQuestionsService {

    private static final Logger logger = Logger.getLogger(ChoiceQuestionsServiceImpl.class.getName());

    @Autowired
    private ChoiceTextMapper choiceTextMapper;

    @Autowired
    private ChoiceQuestionsMapper choiceQuestionsMapper;

    @Override
    public Map<String, Object> getChoiceQuestionsByTextId(int textId) {
        logger.info("获取文本ID为 " + textId + " 的选择题信息");

        // 由于使用int类型，不需要检查null
        // 可以添加范围验证，确保textId为正数
        if (textId <= 0) {
            logger.warning("文本ID无效: " + textId);
            return null;
        }

        // 创建结果map
        Map<String, Object> result = new HashMap<>();

        // 获取文本信息
        Map<String, Object> textInfo = choiceTextMapper.getTextByTextId(textId);
        result.put("text_info", textInfo);

        // 如果文本不存在，返回包含null text_info的结果
        if (textInfo == null) {
            logger.info("未找到文本ID为 " + textId + " 的文本信息");
            result.put("questions", java.util.Collections.emptyList());
            return result;
        }

        // 获取选择题列表
        List<Map<String, Object>> questions = choiceQuestionsMapper.getQuestionsByTextId(textId);
        result.put("questions", questions);

        logger.info("获取文本ID为 " + textId + " 的选择题信息成功，共获取到 " + questions.size() + " 个题目");

        return result;
    }

    @Override
    public PageResult<ChoiceQuestionTitle> getChoiceTextList(PageQuery pageQuery) {
        // 参数验证
        if (pageQuery.getPage() <= 0) {
            pageQuery.setPage(1);
            logger.info("页码无效，默认使用第1页");
        }
        if (pageQuery.getPageSize() <= 0 || pageQuery.getPageSize() > 100) {
            pageQuery.setPageSize(5);
            logger.info("每页大小无效，默认使用5条每页");
        }

        //使用PageHelper工具类
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getPageSize());
        Page<ChoiceQuestionTitle> textList = (Page<ChoiceQuestionTitle>) choiceTextMapper.getTextIdAndTitleList();
        return new PageResult<>(textList.getTotal(), textList.getResult());
    }

}