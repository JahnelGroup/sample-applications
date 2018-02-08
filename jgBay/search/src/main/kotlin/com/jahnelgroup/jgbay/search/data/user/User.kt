package com.jahnelgroup.jgbay.search.data.auction

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "users", refreshInterval = "-1")
data class User (
    @Id
    var id : Long = 0,
    var username: String = "",
    var rating: Short = 0
)