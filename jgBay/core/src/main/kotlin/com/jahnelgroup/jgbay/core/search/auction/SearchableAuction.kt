package com.jahnelgroup.jgbay.core.search.auction

data class SearchableAuction(
    var id : Long = 0,
    var status: String = "",
    var title: String = "",
    var seller: Long = 0,
    var numberOfBids: Int = 0,
    var categories: Set<String> = emptySet()
)