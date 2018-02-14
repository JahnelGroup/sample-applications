package com.jahnelgroup.jgbay.common.search.integration

import com.jahnelgroup.jgbay.common.search.isSearchable
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.PublishSubscribeSpec
import org.springframework.integration.dsl.channel.MessageChannels
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer
import org.springframework.integration.router.PayloadTypeRouter

/**
 * Integration flow for repository events with a source that have the @Searchable annotation.
 */
@Configuration
class RestEventsIntegrationConfig {

    /**
     * Pub/Sub Channel for Rest Events
     */
    @Bean
    fun repositoryEventsPubSubChannel(): PublishSubscribeChannel =
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
        return IntegrationFlows.from(repositoryEventsPubSubChannel())
                .filter(ApplicationEvent::isSearchable)
                .log()
                .route(object : PayloadTypeRouter(){ init {
                    setDefaultOutputChannelName("errorChannel")
                    channelMappings = mapOf(
                        AfterCreateEvent::class.java.name  to "searchCreateChannel",
                        AfterSaveEvent::class.java.name    to "searchUpdateChannel",
                        AfterDeleteEvent::class.java.name  to "searchDeleteChannel")
                }})
                .get()
    }

}