package com.jahnelgroup.jgbay.search.rsql

import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ISOLocalDateTimeRSQLTests extends BaseRSQLTests<LocalDateTime> {

    @Autowired RsqlTestEntityRepository repo
    
    @Override
    String getPropertyName() {
        "timestVal"
    }

    @Override
    List<LocalDateTime> getSortedValues() {
        repo.findAll().stream()
                .map({it.timestVal})
                .sorted()
                .collect()
    }

    @Override
    List<LocalDateTime> getBetweenVals() {
        getSortedValues().subList(0, 2)
    }

    @Override
    boolean lessThan(LocalDateTime val1, LocalDateTime val2) {
        val1 < val2
    }

    @Override
    boolean greaterThan(LocalDateTime val1, LocalDateTime val2) {
        val1 > val2
    }

    @Override
    boolean equals(LocalDateTime val1, LocalDateTime val2) {
        val1 == val2
    }
    
    @Override
    String toString(LocalDateTime val) {
        JsonDateConverter.FORMATTER.format(val)
    }

}
