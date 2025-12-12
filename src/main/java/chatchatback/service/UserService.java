package chatchatback.service;


import chatchatback.pojo.dto.LoginInfoDTO;
import chatchatback.pojo.entity.Grade;

public interface UserService {

    void register(LoginInfoDTO loginInfoDTO);

    LoginInfoDTO login(LoginInfoDTO loginInfoDTO);

    Grade updateGrade(LoginInfoDTO loginInfoDTO);
}
