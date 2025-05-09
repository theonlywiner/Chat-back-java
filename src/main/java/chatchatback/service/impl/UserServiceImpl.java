package chatchatback.service.impl;

import chatchatback.constant.MessageConstant;
import chatchatback.exception.AccountNotFoundException;
import chatchatback.exception.PasswordErrorException;
import chatchatback.mapper.UserMapper;
import chatchatback.pojo.dto.LoginInfoDTO;
import chatchatback.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 注入 BCrypt 加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 注册接口
    @Override
    public void register(LoginInfoDTO loginInfoDTO) {
        // 判断用户名是否已存在
        if (userMapper.getByUsername(loginInfoDTO.getUsername()) != null) {
            throw new AccountNotFoundException(MessageConstant.ALREADY_EXIST);
        }
        // 使用 BCrypt 加密原始密码
        String encodedPassword = passwordEncoder.encode(loginInfoDTO.getPassword());
        userMapper.addUser(loginInfoDTO.getUsername(), encodedPassword);
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

        return user;
    }
}