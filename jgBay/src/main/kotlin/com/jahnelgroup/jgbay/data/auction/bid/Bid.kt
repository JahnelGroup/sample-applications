package com.jahnelgroup.jgbay.data.auction.bid

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jahnelgroup.jgbay.common.data.AbstractEntity
import com.jahnelgroup.jgbay.data.auction.Auction
import com.jahnelgroup.jgbay.data.user.User
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import javax.persistence.*

@Entity
@EntityListeners(value = AuditingEntityListener::class)
data class Bid (
    var amount : BigDecimal = BigDecimal("100.00")
) : AbstractEntity(){

    @ManyToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "createdBy", insertable = false, updatable = false)
    var user : User? = null

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "auctionId", updatable = false)
    var auction : Auction? = null

}