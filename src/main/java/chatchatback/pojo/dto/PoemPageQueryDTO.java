package chatchatback.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoemPageQueryDTO {
    private Integer page = 1;      // 新增页码参数
    private Integer pageSize = 5; // 规范参数名
    private String name;
    private String keyword;     //关键词，查询古文内容
    private Long dynastyId;     //关键词匹配中的朝代
    private Long gradeId;          // 原gradeId改为Long类型
    private Long offset;
    // 内部计算字段（不暴露给前端）
    private transient Long lastId;
}
