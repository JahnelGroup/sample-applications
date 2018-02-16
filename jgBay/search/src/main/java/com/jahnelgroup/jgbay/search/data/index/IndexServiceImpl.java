package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jahnelgroup.jgbay.search.rsql.CustomRSQLOperators;
import com.jahnelgroup.jgbay.search.rsql.es.QueryBuilderRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public JsonNodeDocument findOne(String index, String documentId) {
        try {
            final GetRequest request = new GetRequest(index, index, documentId);
            final GetResponse response = restHighLevelClient.get(request);
            if(!response.isExists()) {
                throw new ResourceNotFoundException("Document not found.");
            }
            return new JsonNodeDocument(objectMapper.readTree(response.getSourceAsString()), index, documentId);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<JsonNode> search(String index, String rsqlString, Pageable pageable) {
        try {
            final Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(rsqlString);
            final QueryBuilder queryBuilder = rootNode.accept(new QueryBuilderRsqlVisitor());
            final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            sourceBuilder.from(pageable.getPageNumber());
            sourceBuilder.size(pageable.getPageSize());
            pageable.getSort().iterator().forEachRemaining(sort ->
                    sourceBuilder.sort(sort.getProperty(), SortOrder.fromString(sort.getDirection().name())));
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            final SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(index);
            searchRequest.source(sourceBuilder);
            final SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            final SearchHits hits = searchResponse.getHits();
            final List<JsonNode> list = Arrays.stream(hits.getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(this::readTree)
                    .collect(Collectors.toList());
            return new PageImpl<>(list, pageable, hits.totalHits);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNodeDocument index(String index, String documentId, JsonNode document) {
        try {
            final String jsonString = objectMapper.writeValueAsString(document);
            restHighLevelClient.index(new IndexRequest(index, index, documentId).source(jsonString, XContentType.JSON));
            return new JsonNodeDocument(document, index, documentId);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode batchIndex(String index, JsonNode batch) {
        try {
            final BulkRequest bulkRequest = new BulkRequest();
            final ArrayNode documents = (ArrayNode) batch.get("documents");
            for(JsonNode document : documents) {
                   bulkRequest.add(new IndexRequest(index, index, document.get("documentId").asText())
                           .source(objectMapper.writeValueAsString(document.get("document")), XContentType.JSON));
            }
            final BulkResponse response = restHighLevelClient.bulk(bulkRequest);
            return objectMapper.convertValue(response, JsonNode.class);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNodeDocument update(String index, String documentId, JsonNode document) {
        try {
            final String jsonString = objectMapper.writeValueAsString(document);
            restHighLevelClient.update(new UpdateRequest(index, index, documentId).doc(jsonString, XContentType.JSON));
            return new JsonNodeDocument(document, index, documentId);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode batchUpdate(String index, JsonNode batch) {
        try {
            final BulkRequest bulkRequest = new BulkRequest();
            final ArrayNode documents = (ArrayNode) batch.get("documents");
            for(JsonNode document : documents) {
                bulkRequest.add(new UpdateRequest(index, index, document.get("documentId").asText())
                        .doc(objectMapper.writeValueAsString(document.get("document")), XContentType.JSON));
            }
            final BulkResponse response = restHighLevelClient.bulk(bulkRequest);
            return objectMapper.convertValue(response, JsonNode.class);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode delete(String index, String documentId) {
        try {
            final DeleteResponse response = restHighLevelClient.delete(
                    new DeleteRequest(index, index, documentId));
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
