package com.jahnelgroup.jgbay.search

import com.jahnelgroup.jgbay.search.auction.SearchableAuction
import com.jahnelgroup.jgbay.search.auction.SearchableAuctionRepo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class SearchConfig(
    @Value("\${elasticsearch.clearOnBoot:false}")
    private var deleteOnStart : Boolean,

    private var searchableAuctionRepo: SearchableAuctionRepo
){

    @PostConstruct
    fun post() {
        if( deleteOnStart ) searchableAuctionRepo.deleteAll()
    }

}