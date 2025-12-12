package chatchatback.controller;

import chatchatback.pojo.dto.Result;
import chatchatback.pojo.dto.SegmentationTechniquesDTO;
import chatchatback.pojo.dto.UserExerciseAnswerDTO;
import chatchatback.pojo.entity.UserExerciseAnswers;
import chatchatback.service.UserExerciseAnswersService;
import chatchatback.utils.CurrentHolder;
import chatchatback.service.SentenceService;
import chatchatback.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 *  句子/断句控制器
 */
@RestController
@Slf4j
@RequestMapping("/sentence-breaking")
@RequiredArgsConstructor
public class SentenceController {

    private final SentenceService sentenceService;
    private final AIService AIService;
    private final UserExerciseAnswersService userExerciseAnswersService;

    /**
     * 获取所有断句技巧
     * @return
     */
    @GetMapping("/skills")
    public Result getSkills() {
        log.info("获取所有断句技巧....");
        return Result.success(sentenceService.getSkills());
    }

    /**
     * ai断句技巧生成题目
     * @return
     */
    @PostMapping("/generate-questions")
    public Result generateQuestions(@RequestBody SegmentationTechniquesDTO segmentationTechniquesDTO) {
        log.info("断句技巧生成题目,{}...", segmentationTechniquesDTO);
        return Result.success(AIService.generateSentenceQuestions(segmentationTechniquesDTO));
    }

    /**
     * 选择断句技巧返回本地题库
     * @return
     */
    @GetMapping("/skills-questions")
    public Result getSkillsQuestions(Integer id) {
        log.info("选择断句技巧返回本地题库,{}...", id);
        return Result.success(sentenceService.getSkillsQuestions(id));
    }

    /**
     * 保存用户提交的答案
     */
    @PostMapping("/answer")
    public Result saveAnswer(@RequestBody UserExerciseAnswerDTO dto) {
        // 校验 questionType
        String qType = dto.getQuestionType();
        if (qType == null || !("manual".equalsIgnoreCase(qType) || "ai".equalsIgnoreCase(qType))) {
            return Result.error("questionType 必须是 'manual' 或 'ai'");
        }

        // 校验 isCorrect
        Integer isCorrect = dto.getIsCorrect();
        if (isCorrect == null || (isCorrect != 0 && isCorrect != 1)) {
            return Result.error("isCorrect 必须是 0 或 1");
        }

        // 1.获取当前用户ID
        Integer currentId = CurrentHolder.getCurrentId();
        Long userId = currentId == null ? null : currentId.longValue();

        UserExerciseAnswers record = UserExerciseAnswers.builder()
                .userId(userId)
                .questionId(dto.getQuestionId())
                .questionType(qType.toLowerCase())
                .userAnswer(dto.getUserAnswer())
                .isCorrect(isCorrect)
                .createdAt(LocalDateTime.now())
                .build();
        userExerciseAnswersService.saveAnswer(record);
        return Result.success();
    }

    /**
     * 获取用户做题记录列表
     */
    @GetMapping("/getuseranswers")
    public Result getUserAnswers() {
        log.info("获取用户做题记录...");
        // 1.获取当前用户ID
        Integer currentId = CurrentHolder.getCurrentId();
        Long userId = currentId == null ? null : currentId.longValue();
        return Result.success(userExerciseAnswersService.getUserAnswers(userId));
    }

    /**
     * 获取用户详情回答情况
     */
    @GetMapping("/getuseranswerdetail")
    public Result getUserAnswerDetail(Long id) {
        log.info("获取用户详情回答情况...,id = {}", id);
        // 1.获取当前用户ID
        Integer currentId = CurrentHolder.getCurrentId();
        Long userId = currentId == null ? null : currentId.longValue();
        return Result.success(userExerciseAnswersService.getUserAnswerDetail(userId, id));
    }
}
