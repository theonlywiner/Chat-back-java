package chatchatback.controller;

import chatchatback.pojo.dto.Result;
import chatchatback.service.QuestionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class QuestionsController {

    @Autowired
    private QuestionsService questionsService;

    /**
     * 根据诗词名称和题目类型查询相关题目，当questionType为null或空字符串时默认返回该文章下所有类型题目
     */
    @GetMapping("/questions/comprehension")
    public Result getQuestionsByPoemNameAndType(@RequestParam String poemName, @RequestParam(required = false) String questionType) {
        // 当questionType为null或空字符串时，设置为null以返回所有类型题目
        if (questionType != null && questionType.trim().isEmpty()) {
            questionType = null;
        }
        log.info("根据诗词名称和题目类型查询题目, 诗词名称: {}, 题目类型: {}", poemName, questionType);
        List<Map<String, Object>> questions = questionsService.getQuestionsByPoemNameAndType(poemName, questionType);
        return Result.success(questions);
    }
}