package com.jahnelgroup.jgbay.data.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jahnelgroup.jgbay.common.data.AbstractEntity
import com.jahnelgroup.jgbay.data.auction.Auction
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.OneToMany

@Entity
@EntityListeners(value = AuditingEntityListener::class)
data class User (

        @JsonIgnore
        @OneToMany(mappedBy = "seller")
        var sellingAuctions : Set<Auction>? = null,

        @JsonIgnore
        @OneToMany
        var biddingAuctions : Set<Auction> = emptySet(),

        var username: String = "",

        var rating : Short = 0

) : AbstractEntity()