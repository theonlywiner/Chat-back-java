package chatchatback.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorProfileVO implements Serializable {
    private int totalWorks;
    private String mostUsedWord;
    private String dynasty;
}
