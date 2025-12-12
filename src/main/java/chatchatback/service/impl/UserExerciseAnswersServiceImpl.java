package chatchatback.service.impl;

import chatchatback.mapper.UserExerciseAnswersMapper;
import chatchatback.pojo.entity.UserExerciseAnswers;
import chatchatback.pojo.vo.UserExerciseAnswersVO;
import chatchatback.service.UserExerciseAnswersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserExerciseAnswersServiceImpl implements UserExerciseAnswersService {

    @Autowired
    private UserExerciseAnswersMapper userExerciseAnswersMapper;

    @Override
    public void saveAnswer(UserExerciseAnswers answer) {
        userExerciseAnswersMapper.insert(answer);
    }

    @Override
    public List<UserExerciseAnswersVO> getUserAnswers(Long userId) {
        // 使用 LambdaQueryWrapper 进行查询
        LambdaQueryWrapper<UserExerciseAnswers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserExerciseAnswers::getUserId, userId);

        // 执行查询并获取结果
        List<UserExerciseAnswers> list = userExerciseAnswersMapper.selectList(queryWrapper);

        // 分组处理不同类型的题目
        Map<String, List<UserExerciseAnswers>> groupedByType = list.stream()
                .collect(Collectors.groupingBy(UserExerciseAnswers::getQuestionType));

        // 创建题目详情映射 (type_questionId -> {sourceText, content})
        Map<String, Map<String, Object>> questionDetails = new HashMap<>();

        // 处理 manual 类型题目
        List<UserExerciseAnswers> manualAnswers = groupedByType.getOrDefault("manual", Collections.emptyList());
        if (!manualAnswers.isEmpty()) {
            // 获取 questionId 列表
            List<Long> ids = manualAnswers.stream()
                    .map(UserExerciseAnswers::getQuestionId)
                    .collect(Collectors.toList());
            if (!ids.isEmpty()) {
                List<Map<String, Object>> manualQuestions = userExerciseAnswersMapper
                        .selectManualQuestionsByIds(ids);
                manualQuestions.forEach(question -> {
                    Long id = ((Number) question.get("id")).longValue();
                    questionDetails.put("manual_" + id, question);
                });
            }
        }

        // 处理 ai 类型题目
        List<UserExerciseAnswers> aiAnswers = groupedByType.getOrDefault("ai", Collections.emptyList());
        if (!aiAnswers.isEmpty()) {
            List<Long> ids = aiAnswers.stream()
                    .map(UserExerciseAnswers::getQuestionId)
                    .collect(Collectors.toList());
            if (!ids.isEmpty()) {
                List<Map<String, Object>> aiQuestions = userExerciseAnswersMapper
                        .selectAIQuestionsByIds(ids);
                aiQuestions.forEach(question -> {
                    Long id = ((Number) question.get("id")).longValue();
                    questionDetails.put("ai_" + id, question);
                });
            }
        }

//        log.info("questionDetails: {}", questionDetails);
        // 转换为 VO 对象并填充题目详情
        return list.stream().map(answer -> {
            UserExerciseAnswersVO vo = new UserExerciseAnswersVO();
            BeanUtils.copyProperties(answer, vo);

            // 根据 type 和 questionId 获取题目详情
            String key = answer.getQuestionType() + "_" + answer.getQuestionId();
            Map<String, Object> questionDetail = questionDetails.get(key);
            if (questionDetail != null) {
                vo.setSourceText((String) questionDetail.get("sourceText"));
                vo.setContent((String) questionDetail.get("content"));
                vo.setAnswer((String) questionDetail.get("answer"));
                vo.setAnalysis((String) questionDetail.get("analysis"));
                vo.setDifficulty((Integer) questionDetail.get("difficulty"));
                vo.setTechniqueId((BigInteger) questionDetail.get("techniqueId"));
            }

            return vo;
        }).toList();
    }

    /**
     * 获取用户详情回答情况
     */
    @Override
    public UserExerciseAnswersVO getUserAnswerDetail(Long userId, Long id) {
        // 1. 根据ID查询用户答题记录
        UserExerciseAnswers userAnswer = userExerciseAnswersMapper.selectById(id);

        // 2. 验证记录是否存在且属于当前用户
        if (userAnswer == null || !userId.equals(userAnswer.getUserId())) {
            return null; // 或抛出异常
        }

        // 3. 根据题目类型查询对应题库表获取题目详情
        Map<String, Object> questionDetail = null;
        Long questionId = userAnswer.getQuestionId();
        String questionType = userAnswer.getQuestionType();

        if ("manual".equals(questionType) && questionId != null) {
            // 查询 exercise_questions 表
            List<Map<String, Object>> manualQuestions = userExerciseAnswersMapper
                    .selectManualQuestionsByIds(Collections.singletonList(questionId));
            if (!manualQuestions.isEmpty()) {
                questionDetail = manualQuestions.get(0);
            }
        } else if ("ai".equals(questionType) && questionId != null) {
            // 查询 ai_exercise_questions 表
            List<Map<String, Object>> aiQuestions = userExerciseAnswersMapper
                    .selectAIQuestionsByIds(Collections.singletonList(questionId));
            if (!aiQuestions.isEmpty()) {
                questionDetail = aiQuestions.get(0);
            }
        }

        // 4. 组合返回详细信息
        UserExerciseAnswersVO detailVO = new UserExerciseAnswersVO();
        BeanUtils.copyProperties(userAnswer, detailVO);

        // 填充题目详情信息
        if (questionDetail != null) {
            detailVO.setSourceText((String) questionDetail.get("sourceText"));
            detailVO.setContent((String) questionDetail.get("content"));
            detailVO.setAnswer((String) questionDetail.get("answer"));
            detailVO.setAnalysis((String) questionDetail.get("analysis"));
            detailVO.setDifficulty((Integer) questionDetail.get("difficulty"));
        }

        return detailVO;
    }
}
