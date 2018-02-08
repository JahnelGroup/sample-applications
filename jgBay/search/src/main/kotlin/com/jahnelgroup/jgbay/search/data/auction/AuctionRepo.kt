package com.jahnelgroup.jgbay.search.data.auction

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface AuctionRepo : ElasticsearchRepository<Auction, Long>
