package com.jahnelgroup.jgbay.search.data.auction

import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.HandleBeforeSave
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Auction::class)
class AuctionHandler{

    @HandleBeforeSave
    @HandleBeforeCreate
    fun handleAuctionCreate(auction : Auction){
        println("Received ${auction}")
    }

}