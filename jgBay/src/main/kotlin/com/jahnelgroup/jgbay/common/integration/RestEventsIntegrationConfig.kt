package com.jahnelgroup.jgbay.common.integration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer

@Configuration
open class RestEventsIntegrationConfig {

    @Bean
    open fun createAndSaveEvents(): ApplicationEventListeningMessageProducer {
        val producer = ApplicationEventListeningMessageProducer()
        producer.setEventTypes(AfterCreateEvent::class.java, AfterSaveEvent::class.java)
        return producer
    }

//    @Bean
//    open fun deleteEvents(): ApplicationEventListeningMessageProducer {
//        val producer = ApplicationEventListeningMessageProducer()
//        producer.setEventTypes(AfterDeleteEvent::class.java)
//        return producer
//    }

}