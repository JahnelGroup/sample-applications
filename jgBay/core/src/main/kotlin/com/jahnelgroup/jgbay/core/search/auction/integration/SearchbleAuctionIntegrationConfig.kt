package com.jahnelgroup.jgbay.core.search.auction.integration

import com.jahnelgroup.jgbay.core.data.auction.Auction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterDeleteEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows

@Configuration
class SearchbleAuctionIntegrationConfig {

    @Bean
    fun upsertAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter{event: RepositoryEvent -> (event is AfterCreateEvent || event is AfterSaveEvent) && event.source is Auction }
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.fromAuction())
                //.handle(Elasticsearch.outboundAdapter(SearchableAuction::class.java, searchableAuctionRepo))
                .get()
    }

    @Bean
    fun deleteAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .filter{event: RepositoryEvent -> event is AfterDeleteEvent && event.source is Auction }
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.fromAuction())
                //.handle(Elasticsearch.outboundAdapter(SearchableAuction::class.java, searchableAuctionRepo).persistMode(Elasticsearch.Companion.PersistMode.DELETE))
                .get()
    }


}