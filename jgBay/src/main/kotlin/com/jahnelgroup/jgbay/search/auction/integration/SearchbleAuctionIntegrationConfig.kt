package com.jahnelgroup.jgbay.search.auction.integration

import com.jahnelgroup.jgbay.data.auction.Auction
import com.jahnelgroup.jgbay.search.auction.SearchableAuction
import com.jahnelgroup.jgbay.search.auction.SearchableAuctionRepo
import org.springframework.beans.BeanUtils
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

//    @Autowired
//    lateinit var deleteEvents: MessageProducerSupport

    @Bean
    fun upsertAuctionESFlow(): IntegrationFlow {
        return IntegrationFlows.from(createAndSaveEvents)
                .filter{event: RepositoryEvent -> (event is AfterCreateEvent) && event.source is Auction}
                .log()
                .handle { payload: AfterCreateEvent, _ ->
//                    var auction = payload.source as Auction
//                    SearchableAuction().apply {
//                        this.id = auction.id!!
//                        this.title = auction.title
//                        this.status = auction.status.name
//                        this.seller = auction.createdBy
//                        this.numberOfBids = auction.bids.size
//                        searchableAuctionRepo.save(this)
//                    }
                    //BeanUtils.copyProperties(payload.source as Auction, searchableAuction)

                    var auction = payload.source
                    val searchableAuction = SearchableAuction()
                    BeanUtils.copyProperties(payload.source as Auction, searchableAuction)
                    println(searchableAuctionRepo.save(searchableAuction))

                }.get()
    }

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