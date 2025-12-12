package chatchatback.service.impl;

import chatchatback.mapper.QuestionsMapper;
import chatchatback.mapper.SentenceMapper;
import chatchatback.pojo.dto.AIGenerateDTO;
import chatchatback.pojo.dto.SegmentationTechniquesDTO;
import chatchatback.pojo.entity.*;
import chatchatback.pojo.vo.AIGenerateVO;
import chatchatback.pojo.vo.SentenceQuestionVO;
import chatchatback.service.AIService;
import chatchatback.utils.CurrentHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static chatchatback.constant.ServiceConstant.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final QuestionsMapper questionsMapper;
    private final SentenceMapper  sentenceMapper;

    // 可复用的RestTemplate和ObjectMapper
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //ai生成题目，返回sessionId
    @Override
    public String generateWordQuestions(AIGenerateDTO aiGenerateDTO) {
        //1.三个字段获取
        Integer userId = CurrentHolder.getCurrentId();
        String sessionId = "session_" + userId + System.currentTimeMillis();
        Integer wordId = aiGenerateDTO.getId();

        AIGenerateVO aiGenerateVO = new AIGenerateVO();
        aiGenerateVO.setId(aiGenerateDTO.getId());
        aiGenerateVO.setWord(aiGenerateDTO.getWord());
        try {
            // 使用常量
            String urlChoice = CHOICE_QUESTIONS_URL;
            String urlBlank = BLANK_QUESTIONS_URL;

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            // 设置请求体类型
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 对象转json
            String jsonBody = objectMapper.writeValueAsString(aiGenerateDTO);
            //  构造HttpEntity
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            // 调用choiceQuestions接口
            String choiceResult = restTemplate.postForObject(urlChoice, request, String.class);
            // 解析choiceQuestions结果
            Map<String, Object> choiceMap = objectMapper.readValue(choiceResult, Map.class);
            List<?> choiceQuestionsListRaw = (List<?>) choiceMap.get("choiceQuestions");
            List<ChoiceQuestions> choiceQuestionsList = objectMapper.convertValue(
                choiceQuestionsListRaw,
                new com.fasterxml.jackson.core.type.TypeReference<List<ChoiceQuestions>>() {}
            );
            aiGenerateVO.setChoiceQuestionsList(choiceQuestionsList);

            // 调用blankQuestions接口
            String blankResult = restTemplate.postForObject(urlBlank, request, String.class);
            // 解析blankQuestions结果
            Map<String, Object> blankMap = objectMapper.readValue(blankResult, Map.class);
            List<?> blankQuestionsListRaw = (List<?>) blankMap.get("blankQuestions");
            List<BlankQuestions> blankQuestionsList = objectMapper.convertValue(
                blankQuestionsListRaw,
                new com.fasterxml.jackson.core.type.TypeReference<List<BlankQuestions>>() {}
            );
            aiGenerateVO.setBlankQuestionsList(blankQuestionsList);
            saveGenerateRecord(userId, sessionId, wordId, choiceQuestionsList, blankQuestionsList);
        } catch (Exception e) {
            log.error("生成题目失败：{}", e.getMessage());
            throw new RuntimeException("生成题目失败");
        }

        log.info("生成常用词题目成功：{}", aiGenerateVO);
        return sessionId;
    }

    // 保存用户题目生成记录及题目明细
    // userId、sessionId、wordId等参数需由调用方传入或在DTO中补充
    // choiceQuestionsList、blankQuestionsList为生成的题目列表
    @Transactional(rollbackFor = Exception.class)
    public void saveGenerateRecord(Integer userId, String sessionId, Integer wordId, List<ChoiceQuestions> choiceQuestionsList, List<chatchatback.pojo.entity.BlankQuestions> blankQuestionsList) {
        // 1. 保存用户题目生成记录表
        UserQuestionSession session = new UserQuestionSession();
        session.setUserId(userId);
        session.setSessionId(sessionId);
        session.setWordId(wordId);
        questionsMapper.insertUserQuestionSession(session);

        // 2. 保存选择题明细
        for (ChoiceQuestions cq : choiceQuestionsList) {
            GeneratedChoiceQuestion gcq = new GeneratedChoiceQuestion();
            gcq.setSessionId(sessionId);
            gcq.setContent(cq.getContent());
            try {
                gcq.setOptions(objectMapper.writeValueAsString(cq.getOptions())); // 用Jackson序列化为JSON字符串
            } catch (JsonProcessingException e) {
                log.error("选项序列化失败", e);
                gcq.setOptions("[]");
            }
            gcq.setAnswer(cq.getAnswer());
            gcq.setAnalysis(cq.getAnalysis());
            questionsMapper.insertGeneratedChoiceQuestion(gcq);
        }

        // 3. 保存填空题明细
        for (BlankQuestions bq : blankQuestionsList) {
            GeneratedBlankQuestion gbq = new GeneratedBlankQuestion();
            gbq.setSessionId(sessionId);
            gbq.setContent(bq.getContent());
            gbq.setAnswer(bq.getAnswer());
            gbq.setAnalysis(bq.getAnalysis());
            questionsMapper.insertGeneratedBlankQuestion(gbq);
        }
    }

    @Override
    public SentenceQuestionVO generateSentenceQuestions(SegmentationTechniquesDTO segmentationTechniquesDTO) {
        Long id = segmentationTechniquesDTO.getId();
        String skill = segmentationTechniquesDTO.getName();
        String description = segmentationTechniquesDTO.getDescription();

        //设置返回类型
        SentenceQuestionVO sentenceQuestionVO = SentenceQuestionVO.builder()
                .id(id)
                .name(skill)
                .description(description)
                .difficulty(segmentationTechniquesDTO.getDifficulty())
                .build();

        try {
            //1.调用接口拿到数据
            String SentenceQuestions = SENTENCE_QUESTIONS_URL;
            // 远程调用句子断句题接口
            String urlSentence = SentenceQuestions;
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();

            // 添加请求头
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构造HttpEntity
            String jsonBody = objectMapper.writeValueAsString(segmentationTechniquesDTO);
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            // 调用句子断句题接口
            String sentenceResult = restTemplate.postForObject(urlSentence, request, String.class);

            // 解析返回结果
            Map<String, Object> sentenceMap = objectMapper.readValue(sentenceResult, Map.class);
            List<?> exerciseQuestionsRaw = (List<?>) sentenceMap.get("data");
            List<ExerciseQuestions> exerciseQuestionsList = new java.util.ArrayList<>();
            for (Object obj : exerciseQuestionsRaw) {
                Map<String, Object> map = (Map<String, Object>) obj;
                Object diffObj = map.get("difficulty");
                if (diffObj instanceof Number) {
                    int diffVal = ((Number) diffObj).intValue();
                    // 1-初级, 2-中级, 3-高级
                    chatchatback.pojo.enums.DifficultyStatus status = null;
                    if (diffVal == 1) status = chatchatback.pojo.enums.DifficultyStatus.EASY;
                    else if (diffVal == 2) status = chatchatback.pojo.enums.DifficultyStatus.MEDIUM;
                    else if (diffVal == 3) status = chatchatback.pojo.enums.DifficultyStatus.HARD;
                    map.put("difficulty", status);
                }
                ExerciseQuestions eq = objectMapper.convertValue(map, ExerciseQuestions.class);
                exerciseQuestionsList.add(eq);
            }
            sentenceQuestionVO.setExerciseQuestionsList(exerciseQuestionsList);
            List<Long> questionIds = saveGenerateSentenceQuestions(sentenceQuestionVO);
            //批量修改id
            for (int i = 0; i < exerciseQuestionsList.size(); i++) {
                exerciseQuestionsList.get(i).setId(questionIds.get(i));
            }
        } catch (Exception e) {
            log.error("生成题目失败：{}", e.getMessage());
            throw new RuntimeException("生成题目失败");
        }
        log.info("生成断句题目成功：{}", sentenceQuestionVO);
        return sentenceQuestionVO;
    }

    public List<Long> saveGenerateSentenceQuestions(SentenceQuestionVO sentenceQuestionVO) {
        //批量插入数组 的方法名 体现批量的意思
        List<ExerciseQuestions> exerciseQuestionsList = sentenceQuestionVO.getExerciseQuestionsList();
        sentenceMapper.insertBachAIQuestions(exerciseQuestionsList, sentenceQuestionVO.getId());
        return exerciseQuestionsList.stream().map(ExerciseQuestions::getId).toList();
    }
}
