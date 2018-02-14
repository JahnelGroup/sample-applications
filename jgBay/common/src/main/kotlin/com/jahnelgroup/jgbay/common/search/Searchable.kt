package com.jahnelgroup.jgbay.common.search

import org.springframework.context.ApplicationEvent
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent

/**
 * An entity marked with this must have a corresponding document in the search service that is
 * mapped to by bean specified by transformRef.
 */
annotation class Searchable(

        /**
         * The name of the resource exposed by the search service.
          */
        val index: String,

        /**
         * The bean name of the SearchableTransformer that should map this Entity to the corresponding
         * resource in the search service.
         */
        val transformRef: String
)

/**
 * Determines if an ApplicationEvent is eligible for Elasticsearch
 */
inline fun ApplicationEvent.isSearchable(): Boolean {
    return if(this.source.javaClass.isAnnotationPresent(Searchable::class.java)){
        if(this is RepositoryEvent){
            (this is AfterCreateEvent || this is AfterSaveEvent || this is AfterDeleteEvent)
        }else{
            true
        }
    }else{
        false
    }
}