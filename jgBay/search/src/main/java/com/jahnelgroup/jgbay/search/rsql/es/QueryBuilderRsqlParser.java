package com.jahnelgroup.jgbay.search.rsql.es;

import com.jahnelgroup.jgbay.search.rsql.AbstractRsqlParser;
import com.jahnelgroup.jgbay.search.rsql.RsqlSearchOperation;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.elasticsearch.index.query.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        String argument = node.getArguments().get(0);
        if(operation != null) {
            switch(operation) {
                case EQUAL: return getEqualsQueryBuilder(node.getSelector(), argument);
                case NOT_EQUAL: return new BoolQueryBuilder().mustNot(new MatchQueryBuilder(node.getSelector(), argument));
                case IN: return getInQueryBuilder(node.getSelector(), node.getArguments());
                case NOT_IN: return new BoolQueryBuilder().mustNot(getInQueryBuilder(node.getSelector(), node.getArguments()));
                case IS_NULL: return getNullQueryBuilder(node.getSelector(), argument);
                case GREATER_THAN: return new RangeQueryBuilder(node.getSelector()).gt(argument);
                case GREATER_THAN_OR_EQUAL: return new RangeQueryBuilder(node.getSelector()).gte(argument);
                case LESS_THAN: return new RangeQueryBuilder(node.getSelector()).lt(argument);
                case LESS_THAN_OR_EQUAL: return new RangeQueryBuilder(node.getSelector()).lte(argument);
                case BETWEEN: return new RangeQueryBuilder(node.getSelector()).from(argument)
                        .includeLower(true)
                        .to(node.getArguments().get(1))
                        .includeUpper(true);
            }
        }
        return null;
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