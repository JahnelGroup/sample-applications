package com.jahnelgroup.jgbay.core.data.auction.search

import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.common.search.SearchableTransformer
import org.springframework.stereotype.Component

@Component("auctionSearchTransformer")
class AuctionSearchTransformer : SearchableTransformer<Auction, AuctionSearchTransformer.Companion.SearchableAuction> {

    companion object {
        data class SearchableAuction(
                var id : Long = 0,
                var status: String = "",
                var title: String = "",
                var seller: Long = 0,
                var numberOfBids: Int = 0,
                var categories: Set<String> = emptySet()
        )
    }

    override fun from(from: Any): SearchableAuction =
        with(from as Auction){
            SearchableAuction().apply {
                this.id = from.id!!
                this.title = from.title
                this.status = from.status.name
                this.seller = from.createdBy
                this.numberOfBids = from.bids.size
                this.categories = from.categories.map { it.name }.toSet()
            }
        }

}