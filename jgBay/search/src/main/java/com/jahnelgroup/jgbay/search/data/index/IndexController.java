package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/mappings")
@RestController
public class IndexController {

    @Autowired
    private IndexService indexService;

    @Autowired
    private JsonNodeIndexResourceAssembler jsonNodeIndexResourceAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/{index}")
    @ResponseStatus(HttpStatus.OK)
    public JsonNode findOne(@PathVariable("index") String index) {
        return toJsonNodeResource(indexService.findOne(index));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public JsonNode list() {
        return indexService.list();
    }

    @PostMapping("/{index}")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonNode createIndex(@RequestBody JsonNode payload, @PathVariable("index") String index) {
        return indexService.createIndex(index, payload);
    }

    @DeleteMapping("/{index}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JsonNode delete(@PathVariable("index") String index) {
        return indexService.delete(index);
    }

    private JsonNode toJsonNodeResource(JsonNodeIndex document) {
        final ResourceSupport resource = jsonNodeIndexResourceAssembler.toResource(document);
        final ObjectNode resourceNode = objectMapper.convertValue(resource, ObjectNode.class);
        final ObjectNode docNode = (ObjectNode) document.getIndex();
        resourceNode.fieldNames().forEachRemaining(field -> docNode.set(field, resourceNode.get(field)));
        return docNode;
    }

}
