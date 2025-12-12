package chatchatback.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO {
    private Long id;

    private String userName;

    private String name;

    private String token;

    private Integer gradeId;

    private String gradeName;
}
