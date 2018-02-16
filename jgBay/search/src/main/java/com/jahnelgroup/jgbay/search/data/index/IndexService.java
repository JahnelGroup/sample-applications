package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IndexService {

    JsonNodeDocument findOne(String index, String documentId);
    Page<JsonNode> search(String index, String rsql, Pageable pageable);
    JsonNodeDocument index(String index, String documentId, JsonNode document);
    JsonNode batchIndex(String index, JsonNode document);
    JsonNodeDocument update(String index, String documentId, JsonNode document);
    JsonNode batchUpdate(String index, JsonNode document);
    JsonNode delete(String index, String documentId);

}
