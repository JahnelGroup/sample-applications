package com.jahnelgroup.jgbay.search.rsql.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.persistence.criteria.Predicate;

import com.jahnelgroup.jgbay.search.rsql.RsqlSearchOperation;
import com.jahnelgroup.jgbay.search.rsql.jpa.GenericRsqlSpecification.SearchOperationContext;

@SuppressWarnings("rawtypes")
public abstract class TypedPredicateBuilders {

    public static final Map<Class<?>, TypedPredicateBuilder<?>> REGISTRY;
    static {
        final Map<Class<?>, TypedPredicateBuilder<?>> tmp = new HashMap<>();
        tmp.put(String.class, new StringPredicateBuilder());
        tmp.put(Integer.class, new IntegerPredicateBuilder());
        tmp.put(Long.class, new LongPredicateBuilder());
        tmp.put(Double.class, new DoublePredicateBuilder());
        tmp.put(BigDecimal.class, new BigDecimalPredicateBuilder());
        tmp.put(Boolean.class, new BooleanPredicateBuilder());
        tmp.put(LocalDate.class, new LocalDatePredicateBuilder());
        tmp.put(LocalDateTime.class, new LocalDateTimePredicateBuilder());
        tmp.put(Date.class, new DatePredicateBuilder());
        tmp.put(Enum.class, new EnumPredicateBuilder());
        REGISTRY = Collections.unmodifiableMap(tmp);
    }

    protected static abstract class TypedPredicateBuilder<T extends Comparable<? super T>> {

        private final Map<RsqlSearchOperation, Function<SearchOperationContext<T>, Predicate>> OPERATION_FUNC_MAP = new HashMap<>();

        public TypedPredicateBuilder() {
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.EQUAL, this::equals);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.NOT_EQUAL, this::notEquals);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.GREATER_THAN, this::greaterThan);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.GREATER_THAN_OR_EQUAL, this::greaterThanOrEquals);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.LESS_THAN, this::lessThan);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.LESS_THAN_OR_EQUAL, this::lessThanOrEquals);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.IN, this::in);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.NOT_IN, this::notIn);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.BETWEEN, this::between);
            OPERATION_FUNC_MAP.put(RsqlSearchOperation.IS_NULL, this::isNull);
        }

        public Predicate toPredicate(RsqlSearchOperation operation, SearchOperationContext<T> context) {
            return OPERATION_FUNC_MAP.get(operation).apply(context);
        }

        public Predicate equals(SearchOperationContext<T> context) {
            if(context.getArgument() == null) {
                return context.getBuilder().isNull(context.getPath().get(context.getPropToUse()));
            } else {
                return context.getBuilder().equal(
                        context.getPath().get(context.getPropToUse()),
                        context.getArgument());
            }
        }

        public Predicate notEquals(SearchOperationContext<T> context) {
            return context.getArgument() == null
                    ? context.getBuilder().isNotNull(context.getPath().get(context.getPropToUse()))
                    : context.getBuilder().notEqual(context.getPath().get(context.getPropToUse()), context.getArgument());
        }

        public Predicate greaterThan(SearchOperationContext<T> context) {
            return context.getBuilder().greaterThan(
                    context.getPath().get(context.getPropToUse()),
                    context.getArgument());
        }

        public Predicate lessThan(SearchOperationContext<T> context) {
            return context.getBuilder().lessThan(
                    context.getPath().get(context.getPropToUse()),
                    context.getArgument());
        }

        public Predicate greaterThanOrEquals(SearchOperationContext<T> context) {
            return context.getBuilder().greaterThanOrEqualTo(
                    context.getPath().get(context.getPropToUse()),
                    context.getArgument());
        }

        public Predicate lessThanOrEquals(SearchOperationContext<T> context) {
            return context.getBuilder().lessThanOrEqualTo(
                    context.getPath().get(context.getPropToUse()),
                    context.getArgument());
        }

        public Predicate in(SearchOperationContext<T> context) {
            return context.getPath().get(context.getPropToUse()).in(context.getArgs());
        }

        public Predicate notIn(SearchOperationContext<T> context) {
            return context.getBuilder().not(in(context));
        }

        public Predicate between(SearchOperationContext<T> context) {
                return context.getBuilder().between(
                        context.getPath().get(context.getPropToUse()),
                        context.getArgument(),
                        context.getArgs().get(1));
        }

        public Predicate isNull(SearchOperationContext<T> context) {
            return Boolean.valueOf((String)context.getArgument())
                    ? context.getBuilder().isNull(context.getPath().get(context.getPropToUse()))
                    : context.getBuilder().isNotNull(context.getPath().get(context.getPropToUse()));
        }

    }

    private static class StringPredicateBuilder extends TypedPredicateBuilder<String> {

        @Override
        public Predicate equals(SearchOperationContext<String> context) {
            return context.getBuilder().like(
                    context.getPath().get(context.getPropToUse()),
                    context.getArgument().replace('*', '%'));
        }

        @Override
        public Predicate notEquals(SearchOperationContext<String> context) {
            return context.getBuilder().notLike(
                    context.getPath().get(context.getPropToUse()),
                    context.getArgument().replace('*', '%'));
        }

    }

    private static class IntegerPredicateBuilder extends TypedPredicateBuilder<Integer> {}
    private static class LongPredicateBuilder extends TypedPredicateBuilder<Long> {}
    private static class DoublePredicateBuilder extends TypedPredicateBuilder<Double> {}
    private static class BigDecimalPredicateBuilder extends TypedPredicateBuilder<BigDecimal> {}
    private static class BooleanPredicateBuilder extends TypedPredicateBuilder<Boolean> {}
    private static class LocalDatePredicateBuilder extends TypedPredicateBuilder<LocalDate> {}
    private static class LocalDateTimePredicateBuilder extends TypedPredicateBuilder<LocalDateTime> {}
    private static class DatePredicateBuilder extends TypedPredicateBuilder<Date> {}
    private static class EnumPredicateBuilder<T extends Enum<T>> extends TypedPredicateBuilder<T> {}

    public static TypedPredicateBuilder<?> from(Class<?> clazz) {
        return REGISTRY.get(clazz.isEnum() ? Enum.class : clazz);
    }

}
