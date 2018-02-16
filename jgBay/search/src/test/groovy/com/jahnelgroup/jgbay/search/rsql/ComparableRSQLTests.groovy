package com.jahnelgroup.jgbay.search.rsql

abstract class ComparableRSQLTests<T extends Comparable<T>> extends BaseRSQLTests<T> {

    @Override
    boolean lessThan(T val1, T val2) {
        val1 < val2
    }

    @Override
    boolean greaterThan(T val1, T val2) {
        val1 > val2
    }

    @Override
    boolean equals(T val1, T val2) {
        val1 == val2
    }

}
