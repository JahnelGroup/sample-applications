package com.jahnelgroup.jgbay.search.data;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.client.ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
@RequestMapping(produces = "application/json")
public class ElasticsearchExceptionHandler {

    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<ApiError> handleResponseException(final ResponseException e) {
        final HttpStatus status = HttpStatus.valueOf(e.getResponse().getStatusLine().getStatusCode());
        return new ResponseEntity<>(new ApiError(status, e.getMessage()), status);
    }

    @ExceptionHandler(ElasticsearchStatusException.class)
    public ResponseEntity<ApiError> handleElasticsearchStatusException(final ElasticsearchStatusException e) {
        final HttpStatus status = HttpStatus.valueOf(e.status().getStatus());
        return new ResponseEntity<>(new ApiError(status, e.getMessage()), status);
    }

}