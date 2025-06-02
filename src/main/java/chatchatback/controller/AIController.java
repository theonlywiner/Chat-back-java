package chatchatback.controller;

import chatchatback.pojo.dto.AIGenerateDTO;
import chatchatback.pojo.dto.QuestionsPageQueryDTO;
import chatchatback.pojo.dto.Result;
import chatchatback.service.AIService;
import chatchatback.service.QuestionsService;
import chatchatback.utils.CurrentHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static java.lang.Thread.sleep;

/**
 * AI接口
 */

@Slf4j
@RestController
@RequestMapping ("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final QuestionsService questionsService;

    /**
     * 生成问题 返回id
     */
    @PostMapping("/generate-questions")
    public Result generateQuestions(@RequestBody AIGenerateDTO aiGenerateDTO) {
        log .info("调用ai接口生成问题....,aiGenerateDTO:{}", aiGenerateDTO);
        Object  result = aiService.generateQuestions(aiGenerateDTO);
        return Result.success(result);
    }

    /**
     * 根据用户id，创建时间，词查询题目历史记录
     */
     @GetMapping("/questions-history")
     public Result getQuestionsHistory(QuestionsPageQueryDTO questionsPageQueryDTO) {
         log .info("查询用户的题目历史记录...，,{}", questionsPageQueryDTO);
         Integer currentUserId = CurrentHolder.getCurrentId();
//         log .info("当前用户id为：{}", currentUserId);
         return Result.success(questionsService.getQuestionsHistory(currentUserId, questionsPageQueryDTO));
     }

     /**
      * 根据题目sessionId，查看题目信息
      */
     @GetMapping("/questionDetail")
     public Result getQuestionsBySessionId(String sessionId) {
          log .info("根据题目id查询题目信息...，,{}", sessionId);
          return Result.success(questionsService .getQuestionsBySessionId(sessionId));
     }

     /**
      * 根据sessionId，删除对应题目信息
      */
      @DeleteMapping("/deleteQuestions")
      public Result deleteQuestions(String sessionId) {
           log .info("根据题目id删除题目信息...，,{}", sessionId);
          questionsService.deleteQuestions(sessionId);
            return Result.success("删除成功");
      }
}
