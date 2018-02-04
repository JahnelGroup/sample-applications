package com.jahnelgroup.jgbay.common.integration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer

@Configuration
class RestEventsIntegrationConfig {

    @Bean
    fun repositoryEvents(): ApplicationEventListeningMessageProducer {
        val producer = ApplicationEventListeningMessageProducer()
        producer.setEventTypes(RepositoryEvent::class.java)
        return producer
    }

}