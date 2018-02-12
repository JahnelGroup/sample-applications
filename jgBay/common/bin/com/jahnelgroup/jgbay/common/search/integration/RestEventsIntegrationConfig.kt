package com.jahnelgroup.jgbay.common.search.integration

import com.jahnelgroup.jgbay.common.search.Searchable
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
import org.springframework.integration.router.PayloadTypeRouter
import org.springframework.messaging.MessageChannel

@Configuration
class RestEventsIntegrationConfig {

    /**
     * Pub/Sub Channel for Rest Events
     */
    @Bean
    fun repositoryEventsPubSubChannel(): MessageChannel =
        MessageChannels.publishSubscribe<PublishSubscribeSpec>("repositoryEventsPubSubChannel").get()

    /**
     * Listen for the RepositoryEvents and publish them on a pub/sub channel.
     */
    @Bean
    fun repositoryEvents(): ApplicationEventListeningMessageProducer {
        val producer = ApplicationEventListeningMessageProducer()
        producer.setEventTypes(RepositoryEvent::class.java)
        producer.setOutputChannelName("repositoryEventsPubSubChannel")
        return producer
    }

    /**
     * Route RepositoryEvents to the proper search Channel if they are annotated with Searchable.
     */
    @Bean
    fun repoEventToSearchRouterFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter(this::searchableEntity)
                .filter(this::searchRelatedEvent)
                .log()
                .route(object : PayloadTypeRouter(){ init {
                    setDefaultOutputChannelName("errorChannel")
                    channelMappings = mapOf(
                        Pair(AfterCreateEvent::class.java.name  , "searchCreateChannel"),
                        Pair(AfterSaveEvent::class.java.name    , "searchUpdateChannel"),
                        Pair(AfterDeleteEvent::class.java.name  , "searchDeleteChannel"))
                }})
                .get()
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