package com.jahnelgroup.jgbay.search.rsql.es;

import com.jahnelgroup.jgbay.search.rsql.AbstractRsqlParser;
import com.jahnelgroup.jgbay.search.rsql.RsqlSearchOperation;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@EqualsAndHashCode(callSuper = false)
public class QueryBuilderRsqlParser extends AbstractRsqlParser<QueryBuilder> {

    private static final DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final Set<RsqlSearchOperation> RANGE_OPERATIONS = Arrays.stream(new RsqlSearchOperation[]{
                RsqlSearchOperation.GREATER_THAN,
                RsqlSearchOperation.GREATER_THAN_OR_EQUAL,
                RsqlSearchOperation.LESS_THAN,
                RsqlSearchOperation.LESS_THAN_OR_EQUAL})
            .collect(Collectors.toSet());

    @Override
    public QueryBuilder and(QueryBuilder root, QueryBuilder operand) {
        return new BoolQueryBuilder().must(root).must(operand);
    }

    @Override
    public QueryBuilder or(QueryBuilder root, QueryBuilder operand) {
        return new BoolQueryBuilder().should(root).should(operand);
    }

    public QueryBuilder parseComparisonNode(ComparisonNode node) {
        final RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(node.getOperator());
        final String selector = node.getSelector().replaceAll("_", "");
        String argument = node.getArguments().get(0);
        if(operation != null) {
            switch(operation) {
                case EQUAL: return wrapInNestedIfNecessary(node.getSelector(), getEqualsQueryBuilder(selector, argument));
                case NOT_EQUAL: return wrapInNestedIfNecessary(node.getSelector(), new BoolQueryBuilder().mustNot(new MatchQueryBuilder(selector, argument)));
                case IN: return wrapInNestedIfNecessary(node.getSelector(), getInQueryBuilder(selector, node.getArguments()));
                case NOT_IN: return wrapInNestedIfNecessary(node.getSelector(), new BoolQueryBuilder().mustNot(getInQueryBuilder(selector, node.getArguments())));
                case IS_NULL: return wrapInNestedIfNecessary(node.getSelector(), getNullQueryBuilder(selector, argument));
                case GREATER_THAN: return wrapInNestedIfNecessary(node.getSelector(), new RangeQueryBuilder(selector).gt(argument));
                case GREATER_THAN_OR_EQUAL: return wrapInNestedIfNecessary(node.getSelector(), new RangeQueryBuilder(selector).gte(argument));
                case LESS_THAN: return wrapInNestedIfNecessary(node.getSelector(), new RangeQueryBuilder(selector).lt(argument));
                case LESS_THAN_OR_EQUAL: return wrapInNestedIfNecessary(node.getSelector(), new RangeQueryBuilder(selector).lte(argument));
                case BETWEEN: return wrapInNestedIfNecessary(node.getSelector(), new RangeQueryBuilder(selector).from(argument)
                        .includeLower(true)
                        .to(node.getArguments().get(1))
                        .includeUpper(true));
            }
        }
        return null;
    }

    private QueryBuilder wrapInNestedIfNecessary(String selector, QueryBuilder nested) {
        final Integer nestedCount = StringUtils.countOccurrencesOf(selector, "_");
        if(nestedCount > 0) {
            final String[] propSplit = selector.split("\\.");
            final StringBuilder builder = new StringBuilder();
            final List<String> nestedPaths = new LinkedList<>();
            IntStream.range(0, propSplit.length - 1).forEach(i -> {
                    if(propSplit[i].startsWith("_")) {
                        builder.append(propSplit[i].substring(1));
                        nestedPaths.add(builder.toString());
                    } else {
                        builder.append(propSplit[i]);
                    }
                    builder.append(".");
            });
            Collections.reverse(nestedPaths);
            QueryBuilder inner = nested;
            for(String path : nestedPaths) {
                inner = new NestedQueryBuilder(path, inner, ScoreMode.None);
            }
            return inner;
        }
        return nested;
    }

    private QueryBuilder getNullQueryBuilder(String property, Object isNull) {
        final Boolean val = Boolean.valueOf((String) isNull);
        final QueryBuilder existsQuery = new ExistsQueryBuilder(property);
        return val
                ? new BoolQueryBuilder().mustNot(existsQuery)
                : existsQuery;
    }

    private QueryBuilder getInQueryBuilder(String property, List<String> args) {
        final BoolQueryBuilder bool = new BoolQueryBuilder();
        args.forEach(arg -> bool.should(new MatchQueryBuilder(property, arg)));
        return bool;
    }

    private QueryBuilder getEqualsQueryBuilder(String property, Object arg) {
        if(arg instanceof String && ((String) arg).contains("*")) {
            return new WildcardQueryBuilder(property, (String) arg);
        }
        return new MatchQueryBuilder(property, arg);
    }

    private Object remapDateToString(Object o) {
        if(o instanceof LocalDate) {
            return LDT_FORMATTER.format(((LocalDate) o).atStartOfDay());
        } else if(o instanceof LocalDateTime) {
            return LDT_FORMATTER.format((LocalDateTime) o);
        }
        return o;
    }

}