package com.jahnelgroup.jgbay.search.auction.integration

import com.jahnelgroup.jgbay.data.auction.Auction
import com.jahnelgroup.jgbay.search.auction.SearchableAuction
import com.jahnelgroup.jgbay.search.auction.SearchableAuctionRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.AfterSaveEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.endpoint.MessageProducerSupport

@Configuration
class SearchbleAuctionIntegrationConfig {

    @Autowired
    lateinit var searchableAuctionRepo: SearchableAuctionRepo

    @Autowired
    lateinit var createAndSaveEvents: MessageProducerSupport

    @Bean
    fun upsertAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from(createAndSaveEvents)
                .filter{event: RepositoryEvent -> (event is AfterCreateEvent || event is AfterSaveEvent) && event.source is Auction}
                .log()
                .transform(RepositoryEvent::getSource)
                .transform(AuctionTransformers.toSearchableAuction())
                .handle { payload: SearchableAuction, _ ->
                    println(searchableAuctionRepo.save(payload))
                }.get()
    }

//    @Bean
//    fun upsertAuctionESFlow(): IntegrationFlow {
//        return IntegrationFlows.from(createAndSaveEvents)
//                .filter{event: RepositoryEvent -> (event is AfterCreateEvent || event is AfterSaveEvent) && event.source is Auction}
//                .log()
//                .transform(RepositoryEvent::getSource)
//                .transform(AuctionTransformers.toSearchableAuction())
//                .handle(Search.outboundAdapter(entityManagerFactory)
//                        .entityClass(SearchableAuction::class.java)
//                        .persistMode(PersistMode.PERSIST)
//                        .flush(true),
//                        { e -> e.transactional() })
//                .get()
//    }

//    @Bean
//    fun deleteAuctionESFlow(): IntegrationFlow {
//        return IntegrationFlows.from(deleteEvents)
//                .filter{event: RepositoryEvent -> event.source is Auction}
//                .log()
//                .handle { payload: AfterCreateEvent, _ ->
//                    var auction = payload.source as Auction
//                    SearchableAuction().apply {
//                        searchableAuctionRepo.delete(auction.id!!)
//                    }
//                }.get()
//    }

}