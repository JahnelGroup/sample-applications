package com.jahnelgroup.jgbay.search.rsql.jpa;

import com.jahnelgroup.jgbay.search.rsql.AbstractRsqlVisitor;
import org.springframework.data.jpa.domain.Specifications;

public class JpaSpecificationRsqlVisitor<T> extends AbstractRsqlVisitor<Specifications<T>> {
    
    public JpaSpecificationRsqlVisitor(Class<T> type) {
        super(new GenericSpecificationRsqlParser<>(type));
    }

}