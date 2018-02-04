package com.jahnelgroup.jgbay.search.auction

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface SearchableAuctionRepo : ElasticsearchRepository<SearchableAuction, Long>
