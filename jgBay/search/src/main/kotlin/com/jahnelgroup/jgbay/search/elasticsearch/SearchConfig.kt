package com.jahnelgroup.jgbay.search.elasticsearch

import com.jahnelgroup.jgbay.search.data.auction.AuctionRepo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class SearchConfig(
    @Value("\${elasticsearch.clearOnBoot:false}")
    private var deleteOnStart : Boolean,

    private var auctionRepo: AuctionRepo
){

    @PostConstruct
    fun post() {
        if( deleteOnStart ) auctionRepo.deleteAll()
    }

}