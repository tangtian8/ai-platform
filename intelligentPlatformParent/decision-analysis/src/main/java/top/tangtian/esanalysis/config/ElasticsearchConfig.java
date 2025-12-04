package top.tangtian.esanalysis.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tangtian
 * @date 2025-12-01 09:44
 */
@Configuration
public class ElasticsearchConfig {
	@Value("${elasticsearch.host:localhost}")
	private String host;

	@Value("${elasticsearch.port:9200}")
	private int port;

	@Bean
	public RestClient restClient() {
		return RestClient.builder(
				new HttpHost(host, port, "http")
		).build();
	}

	@Bean
	public ElasticsearchClient elasticsearchClient(RestClient restClient) {
		return new ElasticsearchClient(
				new RestClientTransport(restClient, new JacksonJsonpMapper())
		);
	}
}
