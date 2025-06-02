package chatchatback.service;

import chatchatback.pojo.dto.AIGenerateDTO;
import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.QuestionsPageQueryDTO;
import chatchatback.pojo.vo.AIGenerateVO;
import chatchatback.pojo.vo.QuestionsHistoryVO;

import java.util.List;

public interface AIService {

    //ai生成问题,返回sessionId
    String generateQuestions(AIGenerateDTO aiGenerateDTO);
}

