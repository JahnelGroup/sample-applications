package com.jahnelgroup.jgbay.search.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jg.elasticsearch.client")
@Data
public class ElasticsearchProperties {

    private String protocol;
    private String hosts;

}
