package chatchatback.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoemPageQueryDTO extends PageQuery{
    private String name;
    private String keyword;     //关键词，查询古文内容
    private Long dynastyId;     //关键词匹配中的朝代


    private Long gradeId;

    private Long offset;
    // 内部计算字段（不暴露给前端）
    private transient Long lastId;
}
