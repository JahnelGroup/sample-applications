package com.jahnelgroup.jgbay.core.data.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jahnelgroup.jgbay.core.data.AbstractEntity
import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.search.Searchable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.OneToMany

@Entity
@EntityListeners(value = AuditingEntityListener::class)
@Searchable(name = "user", transformRef = "userSearchTransformer")
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