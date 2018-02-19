package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;

public interface IndexService {

    JsonNode createIndex(String index, JsonNode indexMapping);
    JsonNodeIndex findOne(String index);
    JsonNode list();
    JsonNode delete(String index);

}
