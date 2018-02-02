package com.jahnelgroup.jgbay.data.auction

import com.jahnelgroup.jgbay.common.data.AbstractEntity
import com.jahnelgroup.jgbay.data.auction.bid.Bid
import com.jahnelgroup.jgbay.data.user.User
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

    @ManyToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "createdBy", insertable = false, updatable = false)
    var seller : User? = null

    enum class Status {
        OPEN, ENDED, CANCELED
    }

    fun addBid(bid: Bid): Auction{
        bid.auction = this
        bids.add(bid)
        return this
    }

    fun removeBid(bid: Bid): Auction{
        bids.remove(bid)
        bid.auction = null
        return this
    }

    fun isAcceptingBids(): Boolean {
        return Status.OPEN == this.status
    }

}