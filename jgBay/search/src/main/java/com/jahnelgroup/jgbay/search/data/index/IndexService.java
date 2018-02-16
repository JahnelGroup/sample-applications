package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IndexService {

    JsonNode findOne(String documentId, String index);
    Page<JsonNode> search(String index, String rsql, Pageable pageable);
    JsonNode index(String documentId, JsonNode document, String index);
    JsonNode update(String documentId, JsonNode document, String index);
    JsonNode delete(String documentId, String index);

}
