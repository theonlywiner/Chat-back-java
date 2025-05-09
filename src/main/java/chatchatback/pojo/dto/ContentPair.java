package chatchatback.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  古文内容对
 * */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentPair implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ancientContent;
    private String modernContent;
}
