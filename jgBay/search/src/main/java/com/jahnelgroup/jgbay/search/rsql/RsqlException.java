package com.jahnelgroup.jgbay.search.rsql;

public class RsqlException extends RuntimeException {

    private static final long serialVersionUID = -4389002379259227055L;

    public RsqlException(String message) {
        super(String.format("Unable to parse RSQL query - please check syntax.  Reason: %s", message));
    }
    
}
