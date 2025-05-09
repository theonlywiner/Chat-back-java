package chatchatback.service;


import chatchatback.pojo.dto.LoginInfoDTO;

public interface UserService {

    void register(LoginInfoDTO loginInfoDTO);

    LoginInfoDTO login(LoginInfoDTO loginInfoDTO);
}
