package chatchatback.service.impl;

import chatchatback.mapper.UserExerciseAnswersMapper;
import chatchatback.pojo.entity.UserExerciseAnswers;
import chatchatback.service.UserExerciseAnswersService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserExerciseAnswersServiceImpl implements UserExerciseAnswersService {

    @Autowired
    private UserExerciseAnswersMapper userExerciseAnswersMapper;

        @Override
        public void saveAnswer(UserExerciseAnswers answer) {
            userExerciseAnswersMapper.insert(answer);
    }
}
