package chatchatback.service;

import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.QuestionsPageQueryDTO;
import chatchatback.pojo.vo.AIGenerateVO;
import chatchatback.pojo.vo.QuestionsHistoryVO;

import java.util.List;
import java.util.Map;

public interface QuestionsService {
    // 获取用户历史问题
    PageResult<QuestionsHistoryVO> getQuestionsHistory(Integer userId, QuestionsPageQueryDTO questionsPageQueryDTO);

    // 获取问题
    AIGenerateVO getQuestionsBySessionId(String sessionId);

    //  根据sessionId，删除问题
    void deleteQuestions(String sessionId);

    // 根据诗词名称和题目类型查询相关题目，当questionType为空时返回该文章下所有类型题目
    List<Map<String, Object>> getQuestionsByPoemNameAndType(String poemName, String questionType);
}
