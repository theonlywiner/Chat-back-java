package chatchatback.service;

import chatchatback.pojo.dto.AIGenerateDTO;
import chatchatback.pojo.dto.SegmentationTechniquesDTO;
import chatchatback.pojo.vo.SentenceQuestionVO;

public interface AIService {

    //ai生成问题,返回sessionId
    String generateWordQuestions(AIGenerateDTO aiGenerateDTO);

    SentenceQuestionVO generateSentenceQuestions(SegmentationTechniquesDTO segmentationTechniquesDTO);
}

