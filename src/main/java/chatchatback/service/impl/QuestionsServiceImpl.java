package chatchatback.service.impl;

import chatchatback.mapper.QuestionsMapper;
import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.QuestionsPageQueryDTO;
import chatchatback.pojo.vo.AIGenerateVO;
import chatchatback.pojo.vo.QuestionsHistoryVO;
import chatchatback.service.QuestionsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionsServiceImpl implements QuestionsService {
    private final QuestionsMapper questionsMapper;
    /**
     * 获取用户题目生成记录
     */
    @Override
    public PageResult<QuestionsHistoryVO> getQuestionsHistory(Integer userId, QuestionsPageQueryDTO questionsPageQueryDTO) {
        //1.设置分页信息
        PageHelper.startPage(questionsPageQueryDTO.getPage(), questionsPageQueryDTO.getPageSize());

        //执行查询
        Page<QuestionsHistoryVO> poemList = (Page<QuestionsHistoryVO>) questionsMapper.getQuestionsHistory(userId, questionsPageQueryDTO);
//        log .info("获取用户题目生成记录成功,{}", poemList.getResult());
        return new PageResult<>(poemList.getTotal(), poemList.getResult());
    }

    /**
     * 根据sessionId查询问题
     */
    @Override
    public AIGenerateVO getQuestionsBySessionId(String sessionId) {
        return questionsMapper.getQuestionsBySessionId(sessionId);
    }

    /**
     * 根据sessionId，删除问题
     */
    @Override
    public void deleteQuestions(String sessionId) {
        questionsMapper.deleteQuestions(sessionId);
    }

    /**
     * 根据诗词名称和题目类型查询相关题目，当questionType为空时返回该文章下所有类型题目
     */
    @Override
    public List<Map<String, Object>> getQuestionsByPoemNameAndType(String poemName, String questionType) {
        return questionsMapper.getQuestionsByPoemNameAndType(poemName, questionType);
    }
}
