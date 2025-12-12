package chatchatback.service.impl;

import chatchatback.constant.Constant;
import chatchatback.constant.MessageConstant;
import chatchatback.exception.AccountNotFoundException;
import chatchatback.exception.PasswordErrorException;
import chatchatback.mapper.GradeMapper;
import chatchatback.mapper.UserMapper;
import chatchatback.pojo.dto.LoginInfoDTO;
import chatchatback.pojo.entity.Grade;
import chatchatback.service.UserService;
import chatchatback.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Currency;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 注入 BCrypt 加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private GradeMapper gradeMapper;

    // 注册接口
    @Override
    public void register(LoginInfoDTO loginInfoDTO) {
        // 判断用户名是否已存在
        if (userMapper.getByUsername(loginInfoDTO.getUsername()) != null) {
            throw new AccountNotFoundException(MessageConstant.ALREADY_EXIST);
        }
        // 使用 BCrypt 加密原始密码
        String encodedPassword = passwordEncoder.encode(loginInfoDTO.getPassword());

        //如果用户没有选择年级，默认为18-全年级
        if (loginInfoDTO.getGradeId() == null) {
            loginInfoDTO.setGradeId(18);
        }else if (loginInfoDTO.getGradeId() > Constant.MaxGradeId || loginInfoDTO.getGradeId() <= 0) {
            throw new PasswordErrorException(MessageConstant.GRADE_ERROR);
        }

        userMapper.addUser(loginInfoDTO.getUsername(), encodedPassword, loginInfoDTO.getGradeId());
    }

    // 登录接口
    @Override
    public LoginInfoDTO login(LoginInfoDTO loginInfoDTO) {
        String username = loginInfoDTO.getUsername();
        String rawPassword = loginInfoDTO.getPassword(); // 原始密码

        LoginInfoDTO user = userMapper.getByUsername(username);
        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 直接对比原始密码和数据库中的 BCrypt 哈希值
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        user.setGradeName(gradeMapper.getGradeNameByGradeId(user.getGradeId()));
        return user;
    }

    // 修改年级
    @Override
    public Grade updateGrade(LoginInfoDTO loginInfoDTO) {
        int id = CurrentHolder.getCurrentId();
        int gradeId = loginInfoDTO.getGradeId();
        if (gradeId > Constant.MaxGradeId || gradeId <= 0) {
            throw new PasswordErrorException(MessageConstant.GRADE_ERROR);
        }

        userMapper.updateGrade(id, gradeId);
        Grade grade = new Grade((long) gradeId, gradeMapper.getGradeNameByGradeId(gradeId));
        return grade;
    }
}