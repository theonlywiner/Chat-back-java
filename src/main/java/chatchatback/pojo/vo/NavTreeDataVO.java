package chatchatback.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *  导航树数据
 * */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 不序列化null值
public class NavTreeDataVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String label;
    private List<NavTreeDataVO> children;
}
