package com.jahnelgroup.jgbay.core.search.auction.integration

import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.search.auction.SearchableAuction
import org.springframework.integration.transformer.GenericTransformer

class AuctionTransformers {

    companion object {
        fun fromAuction() = GenericTransformer<Auction, SearchableAuction> {
            SearchableAuction().apply {
                this.id = it.id!!
                this.title = it.title
                this.status = it.status.name
                this.seller = it.createdBy
                this.numberOfBids = it.bids.size
                this.categories = it.categories.map { it.name }.toSet()
            }
        }
    }

}