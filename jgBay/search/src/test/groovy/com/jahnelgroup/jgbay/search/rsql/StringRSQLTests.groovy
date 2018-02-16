package com.jahnelgroup.jgbay.search.rsql

class StringRSQLTests extends ComparableRSQLTests<String> {

    @Override
    String getPropertyName() {
        "strVal"
    }

    @Override
    List<String> getSortedValues() {
        ["aa", "bb", "cc"]
    }

    @Override
    List<String> getBetweenVals() {
        ["aa", "bb"]
    }

}
