package com.jahnelgroup.jgbay.search.auction

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "auction", shards = 1, replicas = 0, refreshInterval = "-1")
data class SearchableAuction(
    @Id
    var id : Long = 0,
    var status: String = "",
    var title: String = "",
    var seller: Long = 0,
    var numberOfBids: Int = 0
)