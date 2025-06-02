package chatchatback.service;

import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.QuestionsPageQueryDTO;
import chatchatback.pojo.vo.AIGenerateVO;
import chatchatback.pojo.vo.QuestionsHistoryVO;

public interface QuestionsService {
    // 获取用户历史问题
    PageResult<QuestionsHistoryVO> getQuestionsHistory(Integer userId, QuestionsPageQueryDTO questionsPageQueryDTO);

    // 获取问题
    AIGenerateVO getQuestionsBySessionId(String sessionId);

    //  根据sessionId，删除问题
    void deleteQuestions(String sessionId);
}
