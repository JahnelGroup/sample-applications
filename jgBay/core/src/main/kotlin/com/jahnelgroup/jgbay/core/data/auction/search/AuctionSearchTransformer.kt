package com.jahnelgroup.jgbay.core.data.auction.search

import com.jahnelgroup.jgbay.common.search.SearchableTransformer
import com.jahnelgroup.jgbay.core.data.auction.Auction
import org.springframework.stereotype.Component

@Component("auctionSearchTransformer")
class AuctionSearchTransformer : SearchableTransformer<Auction> {

    override fun from(from: Auction): Any =
        object {
            var id : Long           = from.id!!
            var status: String      = from.status.name
            var title: String       = from.title
            var sellerId: Long      = from.createdBy
            var numberOfBids: Int   = from.bids.size
            var categories: Set<String> = from.categories.map { it.name }.toSet()
        }

}