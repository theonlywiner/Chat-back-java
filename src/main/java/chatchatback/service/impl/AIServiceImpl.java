package chatchatback.service.impl;

import chatchatback.mapper.QuestionsMapper;
import chatchatback.pojo.dto.AIGenerateDTO;
import chatchatback.pojo.entity.*;
import chatchatback.pojo.vo.AIGenerateVO;
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

import static chatchatback.constant.AIServiceConstant.BLANK_QUESTIONS_URL;
import static chatchatback.constant.AIServiceConstant.CHOICE_QUESTIONS_URL;


@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final QuestionsMapper questionsMapper;

    // 可复用的RestTemplate和ObjectMapper
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //ai生成题目，返回sessionId
    @Override
    public String generateQuestions(AIGenerateDTO aiGenerateDTO) {
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

        log.info("生成题目成功：{}", aiGenerateVO);
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
}
