package com.jahnelgroup.jgbay.search.rsql;

import java.util.Set;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class CustomRSQLOperators extends RSQLOperators {

    public static final ComparisonOperator BETWEEN = new ComparisonOperator("=btw=", true);
    public static final ComparisonOperator IS_NULL = new ComparisonOperator("=isnull=", false);
    
    public static Set<ComparisonOperator> customOperators() {
        final Set<ComparisonOperator> defaultOperators = RSQLOperators.defaultOperators();
        defaultOperators.add(BETWEEN);
        defaultOperators.add(IS_NULL);
        return defaultOperators;
    }
    
}
