package com.jahnelgroup.jgbay.search.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "jg.elasticsearch.client")
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(ElasticsearchProperties elasticsearchProperties) {
        return new RestHighLevelClient(RestClient.builder(buildHosts(elasticsearchProperties)));
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    public HttpHost[] buildHosts(ElasticsearchProperties elasticsearchProperties) {
        final Function<String, String[]> portFunction = str -> "https".equals(elasticsearchProperties.getProtocol()) ? new String[]{str, "-1"} : str.split(":");
        return Arrays.stream(elasticsearchProperties.getHosts().split(","))
                .map(portFunction)
                .map(hostArgs -> new HttpHost(hostArgs[0], Integer.parseInt(hostArgs[1]), elasticsearchProperties.getProtocol()))
                .collect(Collectors.toList())
                .toArray(new HttpHost[]{});
    }

}
