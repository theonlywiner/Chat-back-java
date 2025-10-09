package chatchatback.service;

import chatchatback.pojo.entity.UserExerciseAnswers;

public interface UserExerciseAnswersService {
    /**
     * 保存用户做题记录
     * @param answer 要保存的实体，userId 与 createdAt 可由调用方填充
     * @return 插入是否成功（true if inserted）
     */
    void saveAnswer(UserExerciseAnswers answer);
}
