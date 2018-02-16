package com.jahnelgroup.jgbay.search.rsql.jpa;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the entity class that must be used for sub-queries.<br/>
 * This is done since JPA does not allow sub-queries creation for table names.<br/>
 * Use this for fields annotated with ElementCollection.
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface SubqueryEntity {
    /**
     * The entity type to be used for sub-queries when annotated field is queried.
     */
    Class<?> value();
}
