package chatchatback.controller;

import chatchatback.constant.JwtClaimsConstant;
import chatchatback.pojo.dto.LoginInfoDTO;
import chatchatback.pojo.dto.Result;
import chatchatback.pojo.vo.UserLoginVO;
import chatchatback.properties.JwtProperties;
import chatchatback.service.UserService;
import chatchatback.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *  用户信息操作
 */

@Validated
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    // 登录接口
    @PostMapping("/users/login")
    public Result login(@RequestBody LoginInfoDTO loginInfoDTO) {
        log.info("登录接口,参数:{}", loginInfoDTO);

        LoginInfoDTO loginInfoDTOGet = userService.login(loginInfoDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, loginInfoDTOGet.getId());
        String token = JwtUtils.generateJwt(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

//        .builder()方法构建对象， 前提是对象上面有@Builder注解
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(loginInfoDTOGet.getId())
                .userName(loginInfoDTOGet.getUsername())
                .name(loginInfoDTOGet.getName())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    // 注册接口 @Valid触发验证
    @PostMapping("/users/register")
    public Result register(@Valid @RequestBody LoginInfoDTO loginInfoDTO) {
        log.info("注册信息 : {}", loginInfoDTO);

        userService.register(loginInfoDTO);

        return Result.success("恭喜你注册成功~");
    }
}
