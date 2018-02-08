package com.jahnelgroup.jgbay.search.data.auction

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "auctions", refreshInterval = "-1")
data class Auction(
    @Id
    var id : Long = 0,
    var status: String = "",
    var title: String = "",
    var seller: Long = 0,
    var numberOfBids: Int = 0,
    var categories: Set<String> = emptySet()
)