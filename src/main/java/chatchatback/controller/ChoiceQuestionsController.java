package chatchatback.controller;

import chatchatback.pojo.dto.PageQuery;
import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.Result;
import chatchatback.pojo.vo.ChoiceQuestionTitle;
import chatchatback.service.ChoiceQuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 选择题控制器
 * 处理选择题相关的HTTP请求
 */
@RestController
@RequestMapping("/questions")
public class ChoiceQuestionsController {

    private static final Logger logger = Logger.getLogger(ChoiceQuestionsController.class.getName());

    @Autowired
    private ChoiceQuestionsService choiceQuestionsService;

    /**
     * 根据text_id获取文言文文本信息和对应的选择题
     * @param textIdStr 文本ID字符串
     * @param pageQuery 分页查询参数
     * @return 统一格式的Result响应对象
     */
    @GetMapping("/choice/{text_id}")
    public Result getChoiceQuestionsByTextId(@PathVariable("text_id") String textIdStr, PageQuery pageQuery) {
        logger.info("接收到获取文本ID为 " + textIdStr + " 的选择题信息请求，分页参数：页码=" + pageQuery.getPage() + ", 每页大小=" + pageQuery.getPageSize());

        try {

            // 尝试将String转换为int
            int textId;
            try {
                textId = Integer.parseInt(textIdStr);
            } catch (NumberFormatException e) {
                logger.warning("文本ID格式错误：" + textIdStr);
                return Result.error("文本ID必须为有效的数字");
            }

            // 调用service获取数据
            Map<String, Object> data = choiceQuestionsService.getChoiceQuestionsByTextId(textId);

            // 检查文本信息是否存在
            if (data == null || data.get("text_info") == null) {
                logger.info("未找到文本ID为 " + textId + " 的文本信息");
                return Result.error("未找到该文本信息");
            }

            logger.info("处理文本ID为 " + textId + " 的选择题信息请求成功");
            return Result.success(data);
        } catch (Exception e) {
            logger.severe("处理文本ID为 " + textIdStr + " 的选择题信息请求失败: " + e.getMessage());
            return Result.error( "获取选择题信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有文言文文本列表，支持分页
     * @return 包含code、message和分页数据的响应对象
     */
    @GetMapping("/choice_text")
    public Result getChoiceTextList(PageQuery pageQuery) {
        logger.info("接收到获取文言文文本列表请求，页码: " + pageQuery.getPage() + "，每页大小: " + pageQuery.getPageSize());
        PageResult<ChoiceQuestionTitle> data = choiceQuestionsService.getChoiceTextList(pageQuery);
        return Result.success(data);
    }
}