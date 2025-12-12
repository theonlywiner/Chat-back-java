package chatchatback.pojo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorError {
    @JsonProperty("error_type")
    private String errorType;

    private String position;

    @JsonProperty("missing_content")
    private String missingContent;

    private String severity;
}
