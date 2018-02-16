package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class IndexController {

    @Autowired
    private IndexService indexService;

    @Autowired
    private JsonNodeResourceAssembler jsonNodeResourceAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/{index}/{documentId}")
    @ResponseStatus(HttpStatus.OK)
    public JsonNode findOne(@PathVariable("index") String index, @PathVariable("documentId") String documentId) {
        return toJsonNodeResource(indexService.findOne(index, documentId));
    }

    @GetMapping("/{index}")
    @ResponseStatus(HttpStatus.OK)
    public Page<JsonNode> search(
            @PathVariable("index") String index,
            @RequestParam("query") String query,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "250") Integer size,
            @RequestParam(value = "sortCol",required = false, defaultValue = "id") String sortCol,
            @RequestParam(value = "sortDir", required = false, defaultValue = "ASC") String sortDir) {
        return indexService.search(index, query, new PageRequest(page, size, Sort.Direction.fromString(sortDir), sortCol));
    }

    @PostMapping("/{index}/{documentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonNode index(@RequestBody JsonNode payload, @PathVariable("index") String index, @PathVariable("documentId") String documentId) {
        return toJsonNodeResource(indexService.index(index, documentId, payload));
    }

    @PostMapping("/batch/{index}")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonNode bulkIndex(@RequestBody JsonNode payload, @PathVariable("index") String index) {
        return indexService.batchIndex(index, payload);
    }

    @DeleteMapping("/{index}/{documentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JsonNode delete(@PathVariable("index") String index, @PathVariable("documentId") String documentId) {
        return indexService.delete(index, documentId);
    }

    @PutMapping("/{index}/{documentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JsonNode update(@RequestBody JsonNode payload, @PathVariable("index") String index, @PathVariable("documentId") String documentId) {
        return toJsonNodeResource(indexService.update(index, documentId, payload));
    }

    @PutMapping("/batch/{index}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JsonNode bulkUpdate(@RequestBody JsonNode payload, @PathVariable("index") String index) {
        return indexService.batchUpdate(index, payload);
    }

    private JsonNode toJsonNodeResource(JsonNodeDocument document) {
        final ResourceSupport resource = jsonNodeResourceAssembler.toResource(document);
        final ObjectNode resourceNode = objectMapper.convertValue(resource, ObjectNode.class);
        final ObjectNode docNode = (ObjectNode) document.getDocument();
        resourceNode.fieldNames().forEachRemaining(field -> docNode.set(field, resourceNode.get(field)));
        return docNode;
    }

}
