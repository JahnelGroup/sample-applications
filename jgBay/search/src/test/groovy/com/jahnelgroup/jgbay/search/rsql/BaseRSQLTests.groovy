package com.jahnelgroup.jgbay.search.rsql

import com.jahnelgroup.jgbay.search.rsql.jpa.JpaSpecificationRsqlVisitor
import cz.jirutka.rsql.parser.RSQLParser
import cz.jirutka.rsql.parser.ast.Node
import groovy.util.logging.Slf4j
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.ReflectionUtils

import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.stream.Collectors
import java.util.stream.IntStream

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

@RunWith(SpringRunner.class)
@DataJpaTest
@Slf4j("logger")
abstract class BaseRSQLTests<T> {
    
    @Autowired RsqlTestEntityRepository repository

    def pageable = new PageRequest(0, 10, Sort.Direction.ASC, getPropertyName())
    
    protected abstract String getPropertyName()
    protected abstract List<T> getSortedValues()
    protected abstract List<T> getBetweenVals()
    
    protected abstract boolean lessThan(T val1, T val2)
    protected abstract boolean greaterThan(T val1, T val2)
    protected abstract boolean equals(T val1, T val2)

    @Before
    void init() {
        repository.deleteAll()
        def testEntities = [
                new RSQLTestEntity(id: 1L, strVal: 'aa', intVal: 1, boolVal: true, dateVal: LocalDate.now(),
                        timestVal: LocalDateTime.now(), doubleVal: 0.01, enumVal: Lorem.LOREM),
                new RSQLTestEntity(id: 2L, strVal: 'bb', intVal: 2, boolVal: true, dateVal: LocalDate.now().plusDays(1),
                        timestVal: LocalDateTime.now().plusDays(1), doubleVal: 0.02, enumVal: Lorem.IPSUM),
                new RSQLTestEntity(id: 3L, strVal: 'cc', intVal: 3, boolVal: false, dateVal: LocalDate.now().plusDays(2),
                        timestVal: LocalDateTime.now().plusDays(2), doubleVal: 0.03, enumVal: Lorem.DOLOR),
        ]
        repository.save(testEntities)
    }

    @Test
    void test_greater_than() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(getPropertyName(), "=gt=", sortedVals.get(1))
        def page = executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(greaterThan(getFieldValue(page.getContent().get(0)), sortedVals.get(1)))
        
        rsqlQueryString = getRsqlQueryString(getPropertyName(), ">", sortedVals.get(1))
        page = executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(greaterThan(getFieldValue(page.getContent().get(0)), sortedVals.get(1)))
    }
    
    @Test
    void test_greater_than_or_equals() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(getPropertyName(), "=ge=", sortedVals.get(2))
        def page =executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(equals(getFieldValue(page.getContent().get(0)), sortedVals.get(2)))
        
        rsqlQueryString = getRsqlQueryString(getPropertyName(), ">=", sortedVals.get(2))
        page = executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(equals(getFieldValue(page.getContent().get(0)), sortedVals.get(2)))
    }
    
    @Test
    void test_less_than() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(getPropertyName(), "=lt=", sortedVals.get(1))
        def page =executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(lessThan(getFieldValue(page.getContent().get(0)), sortedVals.get(1)))
        
        rsqlQueryString = getRsqlQueryString(getPropertyName(), "<", sortedVals.get(1))
        page = executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(lessThan(getFieldValue(page.getContent().get(0)), sortedVals.get(1)))
    }
    
    @Test
    void test_less_than_or_equals() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(getPropertyName(), "=le=", sortedVals.get(0))
        def page =executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(equals(getFieldValue(page.getContent().get(0)), sortedVals.get(0)))
        
        rsqlQueryString = getRsqlQueryString(getPropertyName(), "<=", sortedVals.get(0))
        page = executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(equals(getFieldValue(page.getContent().get(0)), sortedVals.get(0)))
    }
    
    @Test
    void test_equals() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(getPropertyName(), "==", sortedVals.get(1))
        def page =executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(equals(getFieldValue(page.getContent().get(0)), sortedVals.get(1)))
    }
    
    @Test
    void test_not_equals() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(getPropertyName(), "!=", sortedVals.get(1))
        def page =executeQuery(rsqlQueryString)
        assertEquals(2, page.getContent().size())
        assert getFieldValue(page.getContent()[0]) != sortedVals[1]
        assert getFieldValue(page.getContent()[1]) != sortedVals[1]
    }
    
    @Test
    void test_in() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(
                getPropertyName(),
                "=in=",
                IntStream.range(0, sortedVals.size())
                        .filter({it != 1})
                        .mapToObj({sortedVals.get(it)})
                        .collect(Collectors.toList()))
        def page =executeQuery(rsqlQueryString)
        assertEquals(2, page.getContent().size())
        assert getFieldValue(page.getContent()[0]) != sortedVals[1]
        assert getFieldValue(page.getContent()[1]) != sortedVals[1]
    }
    
    @Test
    void test_not_in() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(
                getPropertyName(),
                "=out=",
                IntStream.range(0, sortedVals.size()).filter({it != 1}).mapToObj({sortedVals.get(it)}).collect(Collectors.toList()))
        def page =executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(equals(sortedVals.get(1), getFieldValue(page.getContent().get(0))))
    }
    
    @Test
    void test_between() {
        final List<T> sortedVals = getSortedValues()
        String rsqlQueryString = getRsqlQueryString(
                getPropertyName(),
                "=btw=",
                getBetweenVals())
        def page =executeQuery(rsqlQueryString)
        assertEquals(2, page.getContent().size())
        assert getFieldValue(page.getContent()[0]) != sortedVals[2]
        assert getFieldValue(page.getContent()[1]) != sortedVals[2]
    }
    
    @Test
    void test_is_null() {
        String rsqlQueryString = getIsNullRsqlQueryString(getPropertyName(), true)
        def page =executeQuery(rsqlQueryString)
        assertEquals(0, page.getContent().size())
        
        rsqlQueryString = getIsNullRsqlQueryString(getPropertyName(), false)
        page = executeQuery(rsqlQueryString)
        assertEquals(3, page.getContent().size())
    }
    
    @Test
    void test_and() {
        final List<T> sortedVals = getSortedValues()
        final String rsqlQueryString = new StringBuilder(getRsqlQueryString(getPropertyName(), "==", sortedVals.get(1)))
                .append(";")
                .append(getRsqlQueryString(getPropertyName(), "!=", sortedVals.get(0)))
                .toString()
        def page =executeQuery(rsqlQueryString)
        assertEquals(1, page.getContent().size())
        assertTrue(equals(getFieldValue(page.getContent().get(0)), sortedVals.get(1)))
    }
    
    @Test
    void test_or() {
        final List<T> sortedVals = getSortedValues()
        final String rsqlQueryString = new StringBuilder(getRsqlQueryString(getPropertyName(), "==", sortedVals.get(0)))
                .append(",")
                .append(getRsqlQueryString(getPropertyName(), "==", sortedVals.get(2)))
                .toString()
        def page =executeQuery(rsqlQueryString)
        assertEquals(2, page.getContent().size())
        assert getFieldValue(page.getContent()[0]) != sortedVals[1]
        assert getFieldValue(page.getContent()[1]) != sortedVals[1]
    }

    protected Page<RSQLTestEntity> executeQuery(String rsqlQueryString) {
        final Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(rsqlQueryString)
        final Specification<RSQLTestEntity> spec = rootNode.accept(new JpaSpecificationRsqlVisitor<RSQLTestEntity>(RSQLTestEntity.class))
        repository.findAll(spec, pageable)
    }

    private String getRsqlQueryString(String propertyName, String operator, T value) {
        return new StringBuilder(propertyName)
                .append(operator)
                .append(toString(value))
                .toString()
    }
    
    private static String getIsNullRsqlQueryString(String propertyName, Boolean value) {
        return new StringBuilder(propertyName)
                .append("=isnull=")
                .append(value)
                .toString()
    }
    
    private String getRsqlQueryString(String propertyName, String operator, List<T> values) {
        return new StringBuilder(propertyName)
                .append(operator)
                .append("(")
                .append(values.stream().map({this.toString(it)}).collect(Collectors.joining(",")))
                .append(")")
                .toString()
    }
    
    protected String toString(T val) {
        return val.toString()
    }
    
    private T getFieldValue(RSQLTestEntity entity) {
        try {
            final Field field = ReflectionUtils.findField(RSQLTestEntity.class, getPropertyName())
            return (T) field.get(entity)
        } catch(Exception e) {
            throw new RuntimeException(e)
        }
    }

}
