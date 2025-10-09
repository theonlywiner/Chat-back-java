package chatchatback.constant;

/**
 * AI相关常量
 */
public class ServiceConstant {
//    public static final String CHOICE_QUESTIONS_URL = "http://localhost:5000/ai/generate-questions/choiceQuestions";
//    public static final String BLANK_QUESTIONS_URL = "http://localhost:5000/ai/generate-questions/blankQuestions";
    public static final String CHOICE_QUESTIONS_URL = "http://121.40.171.211:9090/ai/generate-questions/choiceQuestions";
    public static final String BLANK_QUESTIONS_URL = "http://121.40.171.211:9090/ai/generate-questions/blankQuestions";
    public static final String SENTENCE_QUESTIONS_URL = "http://121.40.171.211:9090/ai/generate-questions/sentence-breaking";
    // Whisper 服务（容器内可直连）
    public static final String WHISPER_SERVICE_URL = "http://whisper-service:5003/transcribe";
}
