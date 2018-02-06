package com.jahnelgroup.jgbay.search.auction.integration

import com.jahnelgroup.jgbay.data.auction.Auction
import com.jahnelgroup.jgbay.integration.elasticsearch.Elasticsearch
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
import org.springframework.integration.jpa.support.PersistMode
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
                .handle(Elasticsearch.outboundAdapter(SearchableAuction::class.java, searchableAuctionRepo))
                .get()
    }

    @Bean
    fun deleteAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter{event: RepositoryEvent -> event is AfterDeleteEvent && event.source is Auction}
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.fromAuction())
                .handle(Elasticsearch.outboundAdapter(SearchableAuction::class.java, searchableAuctionRepo)
                        .persistMode(Elasticsearch.Companion.PersistMode.DELETE))
                .get()

        PersistMode.DELETE
    }


}