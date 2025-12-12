package chatchatback.service;

import chatchatback.pojo.entity.UserExerciseAnswers;
import chatchatback.pojo.vo.UserExerciseAnswersVO;

import java.util.List;

public interface UserExerciseAnswersService {
    /**
     * 保存用户做题记录
     * @param answer 要保存的实体，userId 与 createdAt 可由调用方填充
     * @return 插入是否成功（true if inserted）
     */
    void saveAnswer(UserExerciseAnswers answer);

    /**
     * 获取用户做题记录列表
     * @param userId 用户ID
     * @return 用户做题记录
     */
    List<UserExerciseAnswersVO> getUserAnswers(Long userId);

    /**
     * 获取用户某条做题记录详情
     * @param userId 用户ID
     * @param id 做题记录ID
     * @return 用户做题记录详情
     */
    UserExerciseAnswersVO getUserAnswerDetail(Long userId, Long id);
}
