package com.jahnelgroup.jgbay.search.rsql

class LongRSQLTests extends ComparableRSQLTests<Long> {

    @Override
    String getPropertyName() {
        "id"
    }

    @Override
    List<Long> getSortedValues() {
        [1L, 2L, 3L]
    }

    @Override
    List<Long> getBetweenVals() {
        [1L, 2L]
    }

}
