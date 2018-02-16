package com.jahnelgroup.jgbay.search.rsql;

import lombok.Getter;

@Getter
public class RsqlEnumException extends RuntimeException {

    private final String requestedValue;
    private final Enum[] validValues;
    private final Class<? extends Enum> enumClass;
    private final String path;

    public RsqlEnumException(String requestedValue, Enum[] validValues, Class<? extends Enum> enumClass, String path) {
        super("Invalid enum value");
        this.requestedValue = requestedValue;
        this.validValues = validValues;
        this.enumClass = enumClass;
        this.path = path;
    }
}