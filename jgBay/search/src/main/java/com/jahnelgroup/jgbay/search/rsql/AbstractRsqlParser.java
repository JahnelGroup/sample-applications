package com.jahnelgroup.jgbay.search.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractRsqlParser<T> {

    public T parseNode(Node node) {
        if (node instanceof LogicalNode) {
            return parseLogicalNode((LogicalNode) node);
        } else if (node instanceof ComparisonNode) {
            return parseComparisonNode((ComparisonNode) node);
        }
        return null;
    }

    public T parseLogicalNode(LogicalNode node) {
        final List<T> childResults = node.getChildren().stream()
                .map(this::parseNode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        T result = childResults.get(0);
        if (node.getOperator() == LogicalOperator.AND) {
            for(int i = 1; i < childResults.size(); i++) {
                result = and(result, childResults.get(i));
            }
        } else if (node.getOperator() == LogicalOperator.OR) {
            for(int i = 1; i < childResults.size(); i++) {
                result = or(result, childResults.get(i));
            }
        }

        return result;
    }

    public abstract T and(T root, T operand);
    public abstract T or(T root, T operand);
    public abstract T parseComparisonNode(ComparisonNode node);

}