package com.jahnelgroup.jgbay.core.search.auction.integration

import com.jahnelgroup.jgbay.core.data.auction.Auction
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.http.HttpMethod
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.http.Http
import org.springframework.integration.dsl.support.Transformers

@Configuration
class SearchbleAuctionIntegrationConfig {

    @Value("\${service.search.uri}")
    lateinit var SEARCH_SERVICE_URI: String

    /**
     * POST
     */
    @Bean
    fun createESFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter{event: RepositoryEvent -> event is AfterCreateEvent && event.source is Auction }
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.fromAuction())
                .transform(Transformers.toJson())
                .handle(Http.outboundChannelAdapter("${SEARCH_SERVICE_URI}/auctions").httpMethod(HttpMethod.POST))
                .get()
    }

    /**
     * PATCH
     */
    @Bean
    fun updateESFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter{event: RepositoryEvent -> event is AfterSaveEvent && event.source is Auction }
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.fromAuction())
                .enrichHeaders({it.headerExpression("payloadId", "payload.id")})
                .transform(Transformers.toJson())
                .handle(Http.outboundChannelAdapter("${SEARCH_SERVICE_URI}/auctions/{id}").httpMethod(HttpMethod.PATCH)
                        .uriVariable("id", "headers.payloadId"))
                .get()
    }

    /**
     * DELETE
     */
    @Bean
    fun deleteAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter{event: RepositoryEvent -> event is AfterDeleteEvent && event.source is Auction }
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.fromAuction())
                .enrichHeaders({it.headerExpression("payloadId", "payload.id")})
                .transform(Transformers.toJson())
                .handle(Http.outboundChannelAdapter("${SEARCH_SERVICE_URI}/auctions/{id}").httpMethod(HttpMethod.DELETE)
                        .uriVariable("id", "headers.payloadId"))
                .get()
    }

}