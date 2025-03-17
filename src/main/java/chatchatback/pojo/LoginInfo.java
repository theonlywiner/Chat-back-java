package chatchatback.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  登录信息
 * */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    private Integer id;
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 10, message = "用户名长度必须在1到10个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 15, message = "密码长度必须在6到15个字符之间")
    private String password;
    private String name;
    private String token;
}
