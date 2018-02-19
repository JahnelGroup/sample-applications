package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jahnelgroup.jgbay.search.elasticsearch.ElasticsearchProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ElasticsearchProperties elasticsearchProperties;

    @Override
    public JsonNode createIndex(String index, JsonNode indexMapping) {
        try {
            final CreateIndexRequest request = new CreateIndexRequest(index);
            if(indexMapping.has("settings")) {
                request.settings(objectMapper.writeValueAsString(indexMapping.get("settings")), XContentType.JSON);
            }
            request.mapping(index, objectMapper.writeValueAsString(indexMapping.get("mappings")), XContentType.JSON);
            final CreateIndexResponse response = restHighLevelClient.indices().create(request);
            return objectMapper.convertValue(response, JsonNode.class);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNodeIndex findOne(String index) {
        Exception lastException = null;
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        for(HttpHost host : elasticsearchProperties.buildHosts()) {
            try {
                return new JsonNodeIndex(restTemplate.exchange(
                            host.toURI() + "/" + index,
                            HttpMethod.GET,
                            entity,
                            JsonNode.class)
                        .getBody(), index);
            } catch(Exception e) {
                lastException = e;
            }
        }
        throw new RuntimeException(lastException);
    }

    @Override
    public JsonNode list() {
        Exception lastException = null;
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        for(HttpHost host : elasticsearchProperties.buildHosts()) {
            try {
                return restTemplate.exchange(
                            host.toURI() + "/_cat/indices",
                            HttpMethod.GET,
                            entity,
                            JsonNode.class)
                        .getBody();
            } catch(Exception e) {
                lastException = e;
            }
        }
        throw new RuntimeException(lastException);
    }

    @Override
    public JsonNode delete(String index) {
        try {
            final DeleteIndexResponse response = restHighLevelClient.indices().delete(new DeleteIndexRequest(index));
            return objectMapper.convertValue(response, JsonNode.class);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode readTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

}
