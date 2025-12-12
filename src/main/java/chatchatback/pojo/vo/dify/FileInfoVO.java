package chatchatback.pojo.vo.dify;

import lombok.Data;

/**
 * 文件信息 VO
 */
@Data
public class FileInfoVO {
    private String id;
    private String type;
    private String url;
    private String belongs_to;
}