package com.jahnelgroup.jgbay.search.rsql

class IntegerRSQLTests extends ComparableRSQLTests<Integer> {

    @Override
    String getPropertyName() {
        "intVal"
    }

    @Override
    List<Integer> getSortedValues() {
        [1, 2, 3]
    }

    @Override
    List<Integer> getBetweenVals() {
        [1, 2]
    }

}
