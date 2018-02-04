package com.jahnelgroup.jgbay.search.auction

import com.jahnelgroup.jgbay.common.context.UserContextService
import com.jahnelgroup.jgbay.data.auction.Auction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.webmvc.RepositorySearchesResource
import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

const val FIND_BY_QUERY_LINK : String = "findByQuery"

@Configuration
@RestController
@RequestMapping("/api/auctions/search")
class SearchableAuctionController(
        private val userContextService: UserContextService,
        private val searchableAuctionRepo: SearchableAuctionRepo
) {

//    @Bean
//    fun process(resource: RepositorySearchesResource): RepositorySearchesResource {
//        if (Auction::class.java!!.getName() == resource.domainType.name) {
//            resource.add(linkTo(methodOn(SearchableAuctionController::class.java).findByQuery()).withRel(FIND_BY_QUERY_LINK))
//        }
//        return resource
//    }

    @GetMapping(value = FIND_BY_QUERY_LINK)
    fun findByQuery() : ResponseEntity<Any> {
        println("Query")
        return ResponseEntity.ok(Resources<Resource<SearchableAuction>>(searchableAuctionRepo.findAll().map {
            Resource<SearchableAuction>(it)
        }))
    }


}