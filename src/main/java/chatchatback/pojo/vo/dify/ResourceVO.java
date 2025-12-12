package chatchatback.pojo.vo.dify;

import lombok.Data;

/**
 * 资源引用 VO
 */
@Data
public class ResourceVO {
    private Integer position;
    private String dataset_id;
    private String dataset_name;
    private String document_id;
    private String document_name;
    private String segment_id;
    private Double score;
    private String content;
}