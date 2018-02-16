package com.jahnelgroup.jgbay.search.rsql.jpa;

import com.jahnelgroup.jgbay.search.rsql.RsqlException;
import com.jahnelgroup.jgbay.search.rsql.RsqlSearchOperation;
import com.jahnelgroup.jgbay.search.rsql.TypeUtils;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Data
@SuppressWarnings("unchecked")
public class GenericRsqlSpecification<T> implements Specification<T> {

    private final String propertyPath;
    private final ComparisonOperator operator;
    private final List<String> arguments;

    @Data
    protected static class SearchOperationContext<R> {
        private final CriteriaBuilder builder;
        private final Path<?> path;
        private final String propToUse;
        private final R argument;
        private final List<R> args;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<String> queryPath = asList(propertyPath.split("\\."));
        List<Field> javaPath = TypeUtils.findJavaPropertyRecursive(queryPath, root.getJavaType()).collect(toList());
        Field javaField = javaPath.get(0);
        if (javaField.getAnnotation(Embedded.class) != null) {
            return embeddedPropertyPredicate(root, javaField, javaPath, builder);
        } else if (javaField.getAnnotation(ElementCollection.class) != null) {
            return elementCollectionPredicate(root, query, javaField, queryPath, javaPath, builder);
        } else {
            return ownPropertyPredicate(root, javaPath, builder);
        }
    }

    /**
     * Builds a Spring DATA predicate for the entity own property
     */
    private Predicate ownPropertyPredicate(Path<?> path, List<Field> javaPath, CriteriaBuilder builder) {
        Field lastField = javaPath.get(javaPath.size() - 1);
        List<Object> args = TypeUtils.castValues(lastField.getType(), arguments, propertyPath, RsqlSearchOperation.IS_NULL.equals(RsqlSearchOperation.getSimpleOperator(operator)));
        Object argument = args.get(0);
        final RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(operator);
        SearchOperationContext context = new SearchOperationContext(builder, path, javaPathToSpecPath(javaPath), argument, args);
        return TypedPredicateBuilders.from(argument.getClass()).toPredicate(operation, context);
    }

    /**
     * Builds a Spring DATA predicate for the embedded entity by joining to it's table
     */
    private Predicate embeddedPropertyPredicate(Root<?> root, Field embeddedField, List<Field> javaPath, CriteriaBuilder builder) {
        return ownPropertyPredicate(root.join(embeddedField.getName()), javaPath.subList(1, javaPath.size()), builder);
    }

    /**
     * Builds a Spring DATA predicate for element collection by sub-querying the specified table
     */
    private Predicate elementCollectionPredicate(Root<?> root,
                                                 CriteriaQuery<?> query,
                                                 Field collectionField,
                                                 List<String> queryPath,
                                                 List<Field> javaPath,
                                                 CriteriaBuilder builder) {
        if (!Map.class.isAssignableFrom(collectionField.getType())) {
            throw new RsqlException("Non-map element collections cannot be queried");
        } else if (queryPath.size() != 2) {
            throw new RsqlException("Could not read the embedded collection key");
        }
        SubqueryEntity subqueryAnnotation = collectionField.getAnnotation(SubqueryEntity.class);
        MapKeyColumn mapKeyAnnotation = collectionField.getAnnotation(MapKeyColumn.class);
        Column mapValueAnnotation = collectionField.getAnnotation(Column.class);
        CollectionTable joinAnnotation = collectionField.getAnnotation(CollectionTable.class);
        if (subqueryAnnotation == null) {
            throw new RsqlException("Could not detect the entity to create element collection subquery");
        } else if (mapKeyAnnotation == null) {
            throw new RsqlException("Could not detect the mapping key to create element collection subquery");
        } else if (mapValueAnnotation == null) {
            throw new RsqlException("Could not detect the mapping value to create element collection subquery");
        } else if (joinAnnotation == null || joinAnnotation.joinColumns().length < 1) {
            throw new RsqlException("Could not detect the table join to create element collection subquery");
        }
        Class<?> subqueryEntity = subqueryAnnotation.value();
        String keyColumn = mapKeyAnnotation.name();
        String keyValue = queryPath.get(1);
        String valueColumn = mapValueAnnotation.name();
        String joinColumnEntity = joinAnnotation.joinColumns()[0].referencedColumnName();
        String joinColumnMap = joinAnnotation.joinColumns()[0].name();
        Subquery<?> subQuery = query.subquery(subqueryEntity);
        Root<?> subRoot = subQuery.from(subqueryEntity);
        // sub-query will fail without any select statement, even though exists() should not require selection
        subQuery.select(subRoot.get(joinColumnMap));

        List<Object> args = TypeUtils.castValues(String.class, arguments, propertyPath, RsqlSearchOperation.IS_NULL.equals(RsqlSearchOperation.getSimpleOperator(operator)));
        Object argument = args.get(0);
        RsqlSearchOperation operation = RsqlSearchOperation.getSimpleOperator(operator);
        SearchOperationContext context = new SearchOperationContext(builder, subRoot, valueColumn, argument, args);
        Predicate comparison = TypedPredicateBuilders.from(argument.getClass()).toPredicate(operation, context);

        subQuery.where(
                builder.and(
                        // join statement between source entity and target collection table
                        builder.equal(subRoot.get(joinColumnMap), root.get(joinColumnEntity)),
                        // map key check
                        builder.equal(subRoot.get(keyColumn), keyValue),
                        // map value check
                        comparison
                )
        );

        return builder.exists(subQuery);
    }

    private String javaPathToSpecPath(List<Field> javaPath) {
        return javaPath.stream().map(Field::getName).collect(joining("."));
    }

}