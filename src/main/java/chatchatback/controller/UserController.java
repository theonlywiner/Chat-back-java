package chatchatback.controller;

import chatchatback.pojo.LoginInfo;
import chatchatback.pojo.Result;
import chatchatback.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *  用户信息操作
 */

@Validated
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // 登录接口
    @PostMapping("/users/login")
    public Result login(@RequestBody LoginInfo loginInfo) {
        log.info("登录接口,参数:{}", loginInfo);

        LoginInfo loginInfoGet = userService.login(loginInfo);
        //判断是否有loginInfo
        if (loginInfoGet != null) {
            return Result.success("恭喜你登录成功~", loginInfoGet);
        }

        return Result.error("用户名或密码错误");
    }

    // 注册接口 @Valid触发验证
    @PostMapping("/users/register")
    public Result register(@Valid @RequestBody LoginInfo loginInfo) {
        log.info("注册信息 : {}", loginInfo);

        if (userService.register(loginInfo)) {
            return Result.success("恭喜你注册成功~");
        }

        return Result.error("用户名已存在~");
    }
}
