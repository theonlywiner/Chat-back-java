package chatchatback.mapper;

import chatchatback.pojo.entity.ExerciseQuestions;
import chatchatback.pojo.entity.SegmentationTechniques;
import chatchatback.pojo.vo.SentenceQuestionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SentenceMapper {

    List<SegmentationTechniques> getSkills();

    void insertBachAIQuestions(List<ExerciseQuestions> exerciseQuestionsList, Long id);

    SentenceQuestionVO getSkillsQuestions(Integer id);
}
