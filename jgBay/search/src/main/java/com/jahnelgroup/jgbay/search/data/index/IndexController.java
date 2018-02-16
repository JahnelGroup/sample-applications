package com.jahnelgroup.jgbay.search.data.index;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/{index}/{documentId}")
    @ResponseStatus(HttpStatus.OK)
    public JsonNode findOne(@PathVariable("index") String index, @PathVariable("documentId") String documentId) {
        return indexService.findOne(index, documentId);
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
        return indexService.index(index, documentId, payload);
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
        return indexService.update(index, documentId, payload);
    }

    @PutMapping("/batch/{index}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JsonNode update(@RequestBody JsonNode payload, @PathVariable("index") String index) {
        return indexService.batchUpdate(index, payload);
    }

}
