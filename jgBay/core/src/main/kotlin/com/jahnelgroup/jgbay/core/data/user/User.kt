package com.jahnelgroup.jgbay.core.data.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jahnelgroup.jgbay.core.data.AbstractEntity
import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.common.search.Searchable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
@EntityListeners(AuditingEntityListener::class)
@Searchable(index = "users", transformRef = "userSearchTransformer")
data class User (

        @JsonIgnore
        @OneToMany(fetch = FetchType.EAGER, mappedBy = "seller") // TODO should remove this
        var sellingAuctions : Set<Auction>? = null,

        @JsonIgnore
        @OneToMany(fetch = FetchType.EAGER) // TODO should remove this
        var biddingAuctions : Set<Auction> = emptySet(),

        var username: String = "",

        var rating : Short = 0

) : AbstractEntity()