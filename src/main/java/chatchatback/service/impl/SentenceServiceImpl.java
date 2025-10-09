package chatchatback.service.impl;

import chatchatback.mapper.SentenceMapper;
import chatchatback.pojo.entity.SegmentationTechniques;
import chatchatback.pojo.vo.SentenceQuestionVO;
import chatchatback.service.SentenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SentenceServiceImpl implements SentenceService {

    private final SentenceMapper sentenceMapper;

    @Override
    public List<SegmentationTechniques> getSkills() {
        return sentenceMapper.getSkills();
    }

    @Override
    public SentenceQuestionVO getSkillsQuestions(Integer id) {
        return sentenceMapper.getSkillsQuestions(id);
    }
}
