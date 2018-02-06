package com.jahnelgroup.jgbay.search.data.auction.integration

import org.springframework.context.annotation.Configuration

@Configuration
class SearchbleAuctionIntegrationConfig {

//    @Bean
//    fun upsertAuctionESFlow(): IntegrationFlow {
//        return IntegrationFlows.from("repositoryEventsPubSubChannel")
//                .filter{event: RepositoryEvent -> (event is AfterCreateEvent || event is AfterSaveEvent) && event.source is Auction}
//                .log()
//                .transform(RepositoryEvent::getSource)
//                .transform(AuctionTransformers.fromAuction())
//                .handle(Elasticsearch.outboundAdapter(Auction::class.java, auctionRepo))
//                .get()
//    }
//
//    @Bean
//    fun deleteAuctionESFlow(): IntegrationFlow {
//        return IntegrationFlows.from("repositoryEventsPubSubChannel")
//                .filter{event: RepositoryEvent -> event is AfterDeleteEvent && event.source is Auction}
//                .log()
//                .transform(RepositoryEvent::getSource)
//                .transform(AuctionTransformers.fromAuction())
//                .handle(Elasticsearch.outboundAdapter(Auction::class.java, auctionRepo)
//                        .persistMode(Elasticsearch.Companion.PersistMode.DELETE))
//                .get()
//
//        PersistMode.DELETE
//    }


}