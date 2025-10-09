package chatchatback.utils;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EsParagraphIndexUtil {

    @Autowired
    private RestHighLevelClient esClient;

    public void createParagraphIndex() throws Exception {
        CreateIndexRequest request = new CreateIndexRequest("paragraphs");
        GetIndexRequest getIndexRequest = new GetIndexRequest("paragraphs");
        boolean exists = esClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (exists) {
            System.out.println("索引已存在，跳过创建");
            return;
        }
        request.source(
            "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": {\n" +
            "        \"ik_smart_analyzer\": {\n" +
            "          \"type\": \"custom\",\n" +
            "          \"tokenizer\": \"ik_smart\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\":           { \"type\": \"long\" },\n" +
            "      \"chapterId\":    { \"type\": \"long\" },\n" +
            "      \"ancientText\":  { \"type\": \"text\", \"analyzer\": \"ik_smart_analyzer\" },\n" +
            "      \"modernText\":   { \"type\": \"text\" }\n" +
            "    }\n" +
            "  }\n" +
            "}",
            org.elasticsearch.common.xcontent.XContentType.JSON
        );
        CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("创建索引结果: " + response.isAcknowledged());
    }
}