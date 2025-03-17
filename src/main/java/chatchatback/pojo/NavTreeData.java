package chatchatback.pojo;

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
public class NavTreeData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String label;
    private List<NavTreeData> children;
}
