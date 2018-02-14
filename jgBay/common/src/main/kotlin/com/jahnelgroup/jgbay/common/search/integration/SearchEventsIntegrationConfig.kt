package com.jahnelgroup.jgbay.common.search.integration

import com.jahnelgroup.jgbay.common.search.integration.event.SearchCreateEvent
import com.jahnelgroup.jgbay.common.search.integration.event.SearchDeleteEvent
import com.jahnelgroup.jgbay.common.search.integration.event.SearchUpdateEvent
import com.jahnelgroup.jgbay.common.search.isSearchable
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.PublishSubscribeSpec
import org.springframework.integration.dsl.channel.MessageChannels
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer
import org.springframework.integration.router.PayloadTypeRouter

/**
 * Integration flow for domain events with a source that implement the search related marker interfaces.
 */
@Configuration
class SearchEventsIntegrationConfig {

    /**
     * Pub/Sub Channel for Search Events
     */
    @Bean
    fun searchEventsPubSubChannel(): PublishSubscribeChannel =
            MessageChannels.publishSubscribe<PublishSubscribeSpec>("searchEventsPubSubChannel").get()

    /**
     * Listen for the Search Events and publish them on a pub/sub channel.
     */
    @Bean
    fun searchEvents(): ApplicationEventListeningMessageProducer {
        val producer = ApplicationEventListeningMessageProducer()
        producer.setEventTypes(SearchCreateEvent::class.java,
                SearchUpdateEvent::class.java, SearchDeleteEvent::class.java)
        producer.setOutputChannelName("searchEventsPubSubChannel")
        return producer
    }

    /**
     * Route Search Events to the proper search channel.
     */
    @Bean
    fun searchEventToSearchServiceRouterFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchEventsPubSubChannel())
            .filter({it:Any -> it is ApplicationEvent})
            .filter(ApplicationEvent::isSearchable)
            .log()
            .route(object : PayloadTypeRouter() { init {
                setDefaultOutputChannelName("errorChannel")
                channelMappings = mapOf(
                    SearchCreateEvent::class.java.name to "searchCreateChannel",
                    SearchUpdateEvent::class.java.name to "searchUpdateChannel",
                    SearchDeleteEvent::class.java.name to "searchDeleteChannel")
            }})
            .get()
    }

}