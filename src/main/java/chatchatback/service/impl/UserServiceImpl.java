package chatchatback.service.impl;

import chatchatback.mapper.UserMapper;
import chatchatback.pojo.LoginInfo;
import chatchatback.service.UserService;
import chatchatback.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    //注册接口
    @Override
    public boolean register(LoginInfo loginInfo) {
        //1.判断是否存在相同用户名的用户 反之添加用户
        if (userMapper.findByUsername(loginInfo.getUsername()) != null) {
            return false;
        }
        //2.使用BCrypt加密密码
        String encodedPassword = BCrypt.hashpw(loginInfo.getPassword(), BCrypt.gensalt());
        userMapper.addUser(loginInfo.getUsername(), encodedPassword);
        return true;
    }

    //登录接口
    @Override
    public LoginInfo login(LoginInfo loginInfo) {
        LoginInfo user = userMapper.findByUsername(loginInfo.getUsername());
        //不存在用户名和验证密码是否匹配
        if (user == null || !BCrypt.checkpw(loginInfo.getPassword(), user.getPassword())) return null;

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        String jwt = JwtUtils.generateJwt(claims);

        LoginInfo loginInfoGet = new LoginInfo();
        loginInfoGet.setId(user.getId());
        loginInfoGet.setUsername(user.getUsername());
        loginInfoGet.setToken(jwt);
        return loginInfoGet;
    }
}
