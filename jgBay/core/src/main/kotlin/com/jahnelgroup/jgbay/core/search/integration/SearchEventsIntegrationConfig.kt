package com.jahnelgroup.jgbay.core.search.integration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer
import org.springframework.integration.router.PayloadTypeRouter

@Configuration
class SearchEventsIntegrationConfig {

    /**
     * Listen for the SearchEvents and publish them on a pub/sub channel.
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
     * Route SearchEvents to the proper search channel.
     */
    @Bean
    fun searchEventToSearchServiceRouterFlow(): IntegrationFlow {
        return IntegrationFlows.from("searchEventsPubSubChannel")
                .log()
                .route(object : PayloadTypeRouter() { init {
                    channelMappings = mapOf(
                            Pair(SearchCreateEvent::class.java.name, "searchCreateChannel"),
                            Pair("SearchUpdateEvent", "searchUpdateChannel"),
                            Pair("SearchDeleteEvent", "searchDeleteChannel"))

                    setDefaultOutputChannelName("errorChannel")
                }})
                .get()
    }

}


