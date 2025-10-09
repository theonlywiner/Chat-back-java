package chatchatback.utils;

import chatchatback.mapper.BookMapper;
import chatchatback.pojo.entity.Paragraphs;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EsParagraphDataImportUtil {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private RestHighLevelClient esClient;

    public void importAllParagraphsToEs() throws Exception {
    List<Paragraphs> paragraphsList = bookMapper.selectALL();
    int batchSize = 1000;
    int total = paragraphsList.size();
    int count = 0;

    for (int i = 0; i < total; i += batchSize) {
        BulkRequest bulkRequest = new BulkRequest();
        int end = Math.min(i + batchSize, total);
        List<Paragraphs> batch = paragraphsList.subList(i, end);

        for (Paragraphs p : batch) {
            IndexRequest request = new IndexRequest("paragraphs")
                .id(p.getId().toString())
                .source("id", p.getId(),
                        "chapterId", p.getChapterId(),
                        "ancientText", p.getAncientText(),
                        "modernText", p.getModernText());
            bulkRequest.add(request);
        }

        esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        count += batch.size();
        System.out.println("已导入：" + count + "/" + total);
    }
    System.out.println("导入完成，总数量：" + total);
}
}