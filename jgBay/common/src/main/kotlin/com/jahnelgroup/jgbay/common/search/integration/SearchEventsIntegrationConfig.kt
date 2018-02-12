package com.jahnelgroup.jgbay.common.search.integration

import com.jahnelgroup.jgbay.common.search.integration.event.SearchCreateEvent
import com.jahnelgroup.jgbay.common.search.integration.event.SearchDeleteEvent
import com.jahnelgroup.jgbay.common.search.integration.event.SearchUpdateEvent
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
                setDefaultOutputChannelName("errorChannel")
                channelMappings = mapOf(
                    Pair(SearchCreateEvent::class.java.name, "searchCreateChannel"),
                    Pair(SearchUpdateEvent::class.java.name, "searchUpdateChannel"),
                    Pair(SearchDeleteEvent::class.java.name, "searchDeleteChannel"))


            }})
            .get()
    }

}


