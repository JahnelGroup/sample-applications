package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.ResourceSupport;

@Data
public class JsonNodeDocument implements Identifiable<String> {

    private final JsonNode document;
    private final String index;
    private final String documentId;

    private ResourceSupport resource;

    @Override
    public String getId() {
        return index + "/" + documentId;
    }
}
