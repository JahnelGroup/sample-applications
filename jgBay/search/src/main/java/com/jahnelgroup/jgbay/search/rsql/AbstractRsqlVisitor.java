package com.jahnelgroup.jgbay.search.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AbstractRsqlVisitor<T> implements RSQLVisitor<T, Void> {

    private final AbstractRsqlParser<T> parser;

    @Override
    public T visit(AndNode node, Void param) {
        return parser.parseLogicalNode(node);
    }
 
    @Override
    public T visit(OrNode node, Void param) {
        return parser.parseLogicalNode(node);
    }
 
    @Override
    public T visit(ComparisonNode node, Void params) {
        return parser.parseComparisonNode(node);
    }
    
}