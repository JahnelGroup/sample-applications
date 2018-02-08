package com.jahnelgroup.jgbay.core.search.auction.integration

import com.jahnelgroup.jgbay.core.search.Searchable
import com.jahnelgroup.jgbay.core.search.SearchableTransformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.http.HttpMethod
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.channel.MessageChannels
import org.springframework.integration.dsl.http.Http
import org.springframework.integration.dsl.support.GenericHandler
import org.springframework.integration.dsl.support.Transformers
import org.springframework.messaging.MessageChannel

@Configuration
class SearchbleIntegrationConfig {

    @Value("\${service.search.uri}")
    lateinit var SEARCH_SERVICE_URI: String

    @Autowired lateinit var searchableTransformers: SearchableTransformers

    @Bean fun searchCreateChannel(): MessageChannel = MessageChannels.direct("searchCreateChannel").get()
    @Bean fun searchUpdateChannel(): MessageChannel = MessageChannels.direct("searchUpdateChannel").get()
    @Bean fun searchDeleteChannel(): MessageChannel = MessageChannels.direct("searchDeleteChannel").get()

    fun searchableEvent(event: ApplicationEvent): Boolean =
        event.source.javaClass.isAnnotationPresent(Searchable::class.java)

    /**
     * POST
     */
    @Bean
    fun createESFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchCreateChannel())
                .filter(this::searchableEvent)
                .log()
                .transform(ApplicationEvent::getSource)
                .transform(searchableTransformers)
                .transform(Transformers.toJson())
                .handle(Http.outboundGateway("${SEARCH_SERVICE_URI}/auctions").httpMethod(HttpMethod.POST))
                .handle(GenericHandler<Any> { resp, _ -> println(resp) })
                .get()
    }

    /**
     * PUT
     */
    @Bean
    fun updateESFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchUpdateChannel())
                .filter(this::searchableEvent)
                .log()
                .transform(ApplicationEvent::getSource)
                .transform(searchableTransformers)
                .enrichHeaders({it.headerExpression("payloadId", "payload.id")})
                .transform(Transformers.toJson())

                // TODO extract type (i.e., don't hard code auction)
                .handle(Http.outboundGateway("${SEARCH_SERVICE_URI}/auctions/{id}").httpMethod(HttpMethod.PUT)
                        .uriVariable("id", "headers.payloadId"))
                .handle(GenericHandler<Any> { resp, _ -> println(resp) })
                .get()
    }

    /**
     * DELETE
     */
    @Bean
    fun deleteAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchDeleteChannel())
                .filter(this::searchableEvent)
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(searchableTransformers)
                .enrichHeaders({it.headerExpression("payloadId", "payload.id")})
                .transform(Transformers.toJson())
                .handle(Http.outboundGateway("${SEARCH_SERVICE_URI}/auctions/{id}").httpMethod(HttpMethod.DELETE)
                        .uriVariable("id", "headers.payloadId"))
                .handle(GenericHandler<Any> { resp, _ -> println(resp) })
                .get()
    }

}