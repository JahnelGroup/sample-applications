package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.ResourceSupport;

@Data
public class JsonNodeIndex implements Identifiable<String> {

    private final JsonNode index;
    private final String id;

    private ResourceSupport resource;

}
