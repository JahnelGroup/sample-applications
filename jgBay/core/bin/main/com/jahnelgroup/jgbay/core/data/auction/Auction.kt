package com.jahnelgroup.jgbay.core.data.auction

import com.jahnelgroup.jgbay.core.data.AbstractEntity
import com.jahnelgroup.jgbay.core.data.auction.bid.Bid
import com.jahnelgroup.jgbay.core.data.auction.category.Category
import com.jahnelgroup.jgbay.core.data.user.User
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(value = AuditingEntityListener::class)
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

    fun addBid(bid: Bid): Auction {
        bid.auction = this
        bids.add(bid)
        return this
    }

    fun removeBid(bid: Bid): Auction {
        bids.remove(bid)
        bid.auction = null
        return this
    }

    fun isAcceptingBids(): Boolean {
        return Status.OPEN == this.status
    }

}