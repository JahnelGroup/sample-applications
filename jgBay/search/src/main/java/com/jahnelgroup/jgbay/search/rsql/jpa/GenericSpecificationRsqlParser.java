package com.jahnelgroup.jgbay.search.rsql.jpa;

import com.jahnelgroup.jgbay.search.rsql.AbstractRsqlParser;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specifications;

@Data
@EqualsAndHashCode(callSuper = false)
public class GenericSpecificationRsqlParser<T> extends AbstractRsqlParser<Specifications<T>> {

    private final Class<T> type;

    @Override
    public Specifications<T> and(Specifications<T> root, Specifications<T> operand) {
        return Specifications.where(root).and(operand);
    }

    @Override
    public Specifications<T> or(Specifications<T> root, Specifications<T> operand) {
        return Specifications.where(root).or(operand);
    }

    /**
     * Converts RSQL binary comparison node into Spring DATA Specification.
     */
    public Specifications<T> parseComparisonNode(ComparisonNode node) {
        return Specifications.where(new GenericRsqlSpecification<T>(
                node.getSelector(),
                node.getOperator(),
                node.getArguments()));
    }

}