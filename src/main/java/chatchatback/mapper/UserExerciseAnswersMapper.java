package chatchatback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import chatchatback.pojo.entity.UserExerciseAnswers;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserExerciseAnswersMapper extends BaseMapper<UserExerciseAnswers> {
    List<Map<String, Object>> selectManualQuestionsByIds(@Param("questionIds") List<Long> questionIds);

    List<Map<String, Object>> selectAIQuestionsByIds(@Param("questionIds") List<Long> questionIds);
}