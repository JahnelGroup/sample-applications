package com.jahnelgroup.jgbay.search.rsql

class BigDecimalRSQLTests extends ComparableRSQLTests<BigDecimal> {

    @Override
    String getPropertyName() {
        return "doubleVal"
    }

    @Override
    List<BigDecimal> getSortedValues() {
        [
                BigDecimal.valueOf(0.01000000),
                BigDecimal.valueOf(0.02000000),
                BigDecimal.valueOf(0.03000000)
        ]
    }

    @Override
    List<BigDecimal> getBetweenVals() {
        [BigDecimal.valueOf(0.01000000), BigDecimal.valueOf(0.02000000)]
    }

}
