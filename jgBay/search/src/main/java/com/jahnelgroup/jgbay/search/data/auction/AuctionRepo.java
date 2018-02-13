package com.jahnelgroup.jgbay.search.data.auction;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * As of 2/12/18 QueryDSL is not supported with Spring Data ES yet.
 *
 * https://jira.spring.io/browse/DATAES-219
 *
 * https://github.com/spring-projects/spring-data-elasticsearch/blob/d30b41cc5977118a949dbf887c66d3fa87e77fb2/src/main/java/org/springframework/data/elasticsearch/repository/support/ElasticsearchRepositoryFactory.java#L79-L81
 *
 */
public interface AuctionRepo extends ElasticsearchRepository<Auction, Long>

        /*, QuerydslPredicateExecutor<Auction>*/

        {

}
