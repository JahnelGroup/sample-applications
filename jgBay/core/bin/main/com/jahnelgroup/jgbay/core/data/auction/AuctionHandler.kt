package com.jahnelgroup.jgbay.core.data.auction

import com.jahnelgroup.jgbay.core.context.UserContextService
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component

@Component
@RepositoryEventHandler(Auction::class)
class AuctionHandler(private var userContextService: UserContextService) {

    @HandleBeforeCreate
    fun handleAuctionCreate(auction : Auction){
        auction.seller = userContextService.getCurrentUser()!!
    }

}