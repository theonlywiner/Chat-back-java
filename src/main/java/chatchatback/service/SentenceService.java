package chatchatback.service;

import chatchatback.pojo.entity.SegmentationTechniques;
import chatchatback.pojo.vo.SentenceQuestionVO;

import java.util.List;

public interface SentenceService {
    /**
     * 获取所有断句技巧
     * @return
     */
    List<SegmentationTechniques> getSkills();

    /**
     * 获取指定技巧的题目
     * @param id
     * @return
     */
    SentenceQuestionVO getSkillsQuestions(Integer id);
}
