package com.jahnelgroup.jgbay.core.integration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.dsl.PublishSubscribeSpec
import org.springframework.integration.dsl.channel.MessageChannels
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer
import org.springframework.messaging.MessageChannel

@Configuration
class RestEventsIntegrationConfig {

    @Bean
    fun repositoryEvents(): ApplicationEventListeningMessageProducer {
        val producer = ApplicationEventListeningMessageProducer()
        producer.setEventTypes(RepositoryEvent::class.java)
        producer.setOutputChannelName("repositoryEventsPubSubChannel")
        return producer
    }

    @Bean
    fun repositoryEventsPubSubChannel(): MessageChannel =
            MessageChannels.publishSubscribe<PublishSubscribeSpec>("repositoryEventsPubSubChannel").get()

}