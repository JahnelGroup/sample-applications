package com.jahnelgroup.jgbay.core.integration

import com.jahnelgroup.jgbay.core.search.Searchable
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.PublishSubscribeSpec
import org.springframework.integration.dsl.channel.MessageChannels
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer
import org.springframework.integration.router.HeaderValueRouter
import org.springframework.messaging.MessageChannel

@Configuration
class DomainEventsIntegrationConfig {

    /**
     * Pub/Sub Channel for Domain Events
     */
    @Bean
    fun repositoryEventsPubSubChannel(): MessageChannel =
            MessageChannels.publishSubscribe<PublishSubscribeSpec>("domainEventsPubSubChannel").get()

    /**
     * Listen for the Domain Events and publish them on a pub/sub channel.
     */
    @Bean
    fun repositoryEvents(): ApplicationEventListeningMessageProducer {
        val producer = ApplicationEventListeningMessageProducer()
        //producer.setEventTypes(RepositoryEvent::class.java)
        producer.setOutputChannelName("domainEventsPubSubChannel")
        return producer
    }

    /**
     * Route RepositoryEvents to the proper search Channel if they are annotated with Searchable.
     */
    @Bean
    fun domainEventToSearchRouterFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .log()
                //.route(domainEventToSearchRouter())
                .handle({m -> println("")})
                .get()
    }

    /**
     * Defines the channel mappings for RepoEvents to search channels
     */
    @Bean
    fun domainEventToSearchRouter() : HeaderValueRouter {
        var router = HeaderValueRouter("payloadType")
        router.channelMappings = mapOf(
                Pair("org.springframework.data.rest.core.event.AfterCreateEvent"  , "searchCreateChannel"),
                Pair("org.springframework.data.rest.core.event.AfterSaveEvent"    , "searchUpdateChannel"),
                Pair("org.springframework.data.rest.core.event.AfterDeleteEvent"  , "searchDeleteChannel")
        )
        router.setDefaultOutputChannelName("nullChannel")
        return router
    }

    /**
     * Filters down to Repository events for Entitys annotated with @Searchable
     */
    fun searchableEntity(event: ApplicationEvent): Boolean =
            event.source.javaClass.isAnnotationPresent(Searchable::class.java)

    /**
     * Filters out non-search related Repository Events
     */
    fun searchRelatedEvent(event: ApplicationEvent): Boolean =
            event is AfterCreateEvent || event is AfterSaveEvent || event is AfterDeleteEvent

}
