package com.jahnelgroup.jgbay.data.auction.search

import org.springframework.data.elasticsearch.annotations.Document
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Document(indexName = "auction", shards = 1, replicas = 0, refreshInterval = "-1")
data class SearchableAuction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,

    var title: String? = null,
    var seller: Long? = null,
    var numberOfBids: Int? = null
)