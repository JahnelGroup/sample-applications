package com.jahnelgroup.jgbay.search.data.auction;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AuctionRepo extends ElasticsearchRepository<Auction, Long> {

}
