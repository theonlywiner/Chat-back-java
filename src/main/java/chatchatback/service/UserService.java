package chatchatback.service;


import chatchatback.pojo.LoginInfo;

public interface UserService {

    boolean register(LoginInfo loginInfo);

    LoginInfo login(LoginInfo loginInfo);
}
