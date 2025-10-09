package chatchatback.pojo.vo;

import lombok.Data;
import java.util.List;

@Data
public class DifyEvaluateResultVO {
    private Integer overall_score;
    private DetailedScores detailed_scores;
    private List<MajorError> major_errors;
    private Feedback feedback;

    @Data
    public static class DetailedScores {
        private Integer content_completeness;
        private Integer structural_correctness;
        private Integer key_imagery_preservation;
    }

    @Data
    public static class MajorError {
        private String error_type;
        private String position;
        private String missing_content;
        private String severity;
    }

    @Data
    public static class Feedback {
        private String praise;
        private List<String> suggestions;
    }
}
