package com.jahnelgroup.jgbay.data.auction.search

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface SearchableAuctionRepo : ElasticsearchRepository<SearchableAuction, Long>
