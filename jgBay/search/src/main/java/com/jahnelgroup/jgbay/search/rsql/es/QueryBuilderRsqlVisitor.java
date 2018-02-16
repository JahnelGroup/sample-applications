package com.jahnelgroup.jgbay.search.rsql.es;

import com.jahnelgroup.jgbay.search.rsql.AbstractRsqlVisitor;
import org.elasticsearch.index.query.QueryBuilder;

public class QueryBuilderRsqlVisitor extends AbstractRsqlVisitor<QueryBuilder> {
    
    public QueryBuilderRsqlVisitor() {
        super(new QueryBuilderRsqlParser());
    }

}