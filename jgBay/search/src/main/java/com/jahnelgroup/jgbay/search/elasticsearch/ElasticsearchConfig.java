package com.jahnelgroup.jgbay.search.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "jg.elasticsearch.client")
public class ElasticsearchConfig {

    private String protocol;
    private String hosts;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(buildHosts()));
    }

    public HttpHost[] buildHosts() {
        final Function<String, String[]> portFunction = str -> "https".equals(protocol) ? str.split(":") : new String[]{str, "-1"};
        return Arrays.stream(hosts.split(","))
                .map(portFunction)
                .map(hostArgs -> new HttpHost(hostArgs[0], Integer.parseInt(hostArgs[1]), protocol))
                .collect(Collectors.toList())
                .toArray(new HttpHost[]{});
    }

}
