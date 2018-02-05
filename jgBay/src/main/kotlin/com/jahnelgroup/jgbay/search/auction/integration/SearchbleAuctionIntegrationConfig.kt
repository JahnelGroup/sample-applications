package com.jahnelgroup.jgbay.search.auction.integration

import com.jahnelgroup.jgbay.data.auction.Auction
import com.jahnelgroup.jgbay.search.auction.SearchableAuction
import com.jahnelgroup.jgbay.search.auction.SearchableAuctionRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.endpoint.MessageProducerSupport
import org.springframework.messaging.MessageChannel

@Configuration
class SearchbleAuctionIntegrationConfig {

    @Autowired
    lateinit var searchableAuctionRepo: SearchableAuctionRepo

    @Autowired
    lateinit var repositoryEvents: MessageProducerSupport

    @Autowired
    lateinit var repositoryEventsPubSubChannel: MessageChannel

    @Bean
    fun upsertAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter{event: RepositoryEvent -> (event is AfterCreateEvent || event is AfterSaveEvent) && event.source is Auction}
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.fromAuction())
                // TODO: Can we do this with a JpaOutbound adapter?
                .handle { payload: SearchableAuction, _ ->
                    // TODO: Without the println it's throwing
                    // org.springframework.messaging.core.DestinationResolutionException: no output-channel or replyChannel header available
                    println(searchableAuctionRepo.save(payload))
                }.get()
    }

    @Bean
    fun deleteAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter{event: RepositoryEvent -> event is AfterDeleteEvent && event.source is Auction}
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.fromAuction())
                .handle { payload: SearchableAuction, _ ->
                    // TODO: Without the println it's throwing
                    // org.springframework.messaging.core.DestinationResolutionException: no output-channel or replyChannel header available
                    println(searchableAuctionRepo.delete(payload))
                }.get()
    }


}