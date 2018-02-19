package com.jahnelgroup.jgbay.search.elasticsearch;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "jg.elasticsearch.client")
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(ElasticsearchProperties elasticsearchProperties) {
        return new RestHighLevelClient(RestClient.builder(elasticsearchProperties.buildHosts()));
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

}
