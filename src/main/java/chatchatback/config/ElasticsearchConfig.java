package chatchatback.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

  @Value("${chatchat.es.host}")
  private String esHost;

  @Bean
  public RestHighLevelClient restHighLevelClient() {
    return new RestHighLevelClient(RestClient.builder(
            HttpHost.create(esHost)
    ));
  }
}