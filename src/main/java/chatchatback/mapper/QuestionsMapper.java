package chatchatback.mapper;

import chatchatback.pojo.dto.QuestionsPageQueryDTO;
import chatchatback.pojo.entity.GeneratedBlankQuestion;
import chatchatback.pojo.entity.GeneratedChoiceQuestion;
import chatchatback.pojo.entity.UserQuestionSession;
import chatchatback.pojo.vo.AIGenerateVO;
import chatchatback.pojo.vo.QuestionsHistoryVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionsMapper {
    // 插入用户题目生成记录
    int insertUserQuestionSession(UserQuestionSession session);

    // 插入生成的选择题
    int insertGeneratedChoiceQuestion(GeneratedChoiceQuestion question);

    // 插入生成的填空题
    int insertGeneratedBlankQuestion(GeneratedBlankQuestion question);

    // 获取用户题目生成记录
    List<QuestionsHistoryVO> getQuestionsHistory(Integer userId,  QuestionsPageQueryDTO questionsPageQueryDTO);

    // 根据题目sessionId,获取题目
    AIGenerateVO getQuestionsBySessionId(String sessionId);

    // 删除题目
    @Delete( "delete from user_question_sessions where session_id = #{sessionId};")
    void deleteQuestions(String sessionId);
}
