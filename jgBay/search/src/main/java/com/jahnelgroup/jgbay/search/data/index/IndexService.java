package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IndexService {

    JsonNode findOne(String index, String documentId);
    Page<JsonNode> search(String index, String rsql, Pageable pageable);
    JsonNode index(String index, String documentId, JsonNode document);
    JsonNode update(String index, String documentId, JsonNode document);
    JsonNode delete(String index, String documentId);

}
