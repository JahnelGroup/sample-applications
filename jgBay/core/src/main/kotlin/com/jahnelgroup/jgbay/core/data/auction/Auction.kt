package com.jahnelgroup.jgbay.core.data.auction

import com.jahnelgroup.jgbay.core.data.AbstractEntity
import com.jahnelgroup.jgbay.core.data.auction.bid.Bid
import com.jahnelgroup.jgbay.core.data.auction.category.Category
import com.jahnelgroup.jgbay.core.data.auction.event.BidAddedEvent
import com.jahnelgroup.jgbay.core.data.auction.event.BidRemovedEvent
import com.jahnelgroup.jgbay.core.data.auction.event.BidUpdatedEvent
import com.jahnelgroup.jgbay.core.data.user.User
import com.jahnelgroup.jgbay.core.search.Searchable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(value = AuditingEntityListener::class)
@Searchable("auctions", transformRef = "auctionSearchTransformer")
data class Auction (

        @field:Embedded
        var auctionDetails : AuctionDetails = AuctionDetails(),

        var title : String = "",

        var status : Status = Status.OPEN

) : AbstractEntity() {

    @OneToMany(mappedBy = "auction", cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
    var bids: MutableSet<Bid> = mutableSetOf()

    @ManyToOne
    @JoinColumn(name = "createdBy", insertable = false, updatable = false)
    var seller : User? = null

    @ManyToMany(cascade = arrayOf(CascadeType.ALL))
    var categories: MutableSet<Category> = mutableSetOf()

    enum class Status {
        OPEN, ENDED, CANCELED
    }

    /**
     * Add a new bid to this Auction
     */
    fun addBid(bid: Bid): Auction {
        bid.auction = this
        bids.add(bid)
        registerEvent(BidAddedEvent(this, bid))
        return this
    }

    /**
     * Update an existing bid on this Auction
     */
    fun updateBid(bid: Bid, incoming: Bid): Auction {
        bid.amount = incoming.amount
        registerEvent(BidUpdatedEvent(this, bid, incoming))
        return this
    }

    /**
     * Returns the bid owner by the provided User ID
     */
    fun getBidByUser(userId: Long) = bids.firstOrNull { it.createdBy == userId }


    /**
     * Remove a bid from this Auction
     */
    fun removeBid(bid: Bid): Auction {
        bids.remove(bid)
        bid.auction = null
        registerEvent(BidRemovedEvent(this, bid))
        return this
    }

    fun isAcceptingBids(): Boolean {
        return Status.OPEN == this.status
    }

}