package chatchatback.mapper;

import chatchatback.pojo.dto.QuestionsPageQueryDTO;
import chatchatback.pojo.entity.GeneratedBlankQuestion;
import chatchatback.pojo.entity.GeneratedChoiceQuestion;
import chatchatback.pojo.entity.UserQuestionSession;
import chatchatback.pojo.vo.AIGenerateVO;
import chatchatback.pojo.vo.QuestionsHistoryVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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

    // 根据诗词名称和题目类型查询相关题目，当questionType为空时返回该文章下所有类型题目
    @Select("SELECT q.* FROM questions q WHERE q.id IN " +
            "(SELECT question_id FROM poem_questions pq WHERE pq.poem_id IN " +
            "(SELECT p.id FROM poems p WHERE p.name = #{poemName})) " +
            "AND (#{questionType} IS NULL OR q.question_type = #{questionType})")
    List<Map<String, Object>> getQuestionsByPoemNameAndType(String poemName, String questionType);
}
