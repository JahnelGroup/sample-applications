package com.jahnelgroup.jgbay.search.rsql

import org.junit.Ignore
import org.junit.Test

class EnumRSQLTests extends ComparableRSQLTests<Lorem> {

    @Override
    String getPropertyName() {
        "enumVal"
    }

    @Override
    List<Lorem> getSortedValues() {
        Lorem.values()
    }

    @Override
    List<Lorem> getBetweenVals() {
        Arrays.asList(Lorem.LOREM, Lorem.AMET)
    }

    @Override
    @Ignore
    void test_between() {
    }

    @Override
    @Ignore
    void test_greater_than() {
    }

    @Override
    @Ignore
    void test_greater_than_or_equals() {
    }

    @Override
    @Ignore
    void test_less_than() {
    }

    @Override
    @Ignore
    void test_less_than_or_equals() {
    }

    @Override
    protected String toString(Lorem val) {
        return val.name()
    }

    @Test(expected = RsqlEnumException.class)
    void test_invalid_enum_value() {
        executeQuery("enumVal==FOO")
    }

}
