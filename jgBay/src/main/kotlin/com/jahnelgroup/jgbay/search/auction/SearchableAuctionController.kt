package com.jahnelgroup.jgbay.search.auction

import com.jahnelgroup.jgbay.context.UserContextService
import com.jahnelgroup.jgbay.data.auction.Auction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.webmvc.RepositorySearchesResource
import org.springframework.hateoas.Resource
import org.springframework.hateoas.ResourceProcessor
import org.springframework.hateoas.Resources
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

const val SEARCH: String = "search"

@Configuration
@RestController
@RequestMapping("/api/auctions/search")
class SearchableAuctionController(
        private val userContextService: UserContextService,
        private val searchableAuctionRepo: SearchableAuctionRepo
) {

    @Bean
    fun process() = ResourceProcessor<RepositorySearchesResource> {
        it.apply {
            if (Auction::class.qualifiedName == it.domainType.name) {
                it.add(linkTo(methodOn(SearchableAuctionController::class.java).search()).withRel(SEARCH))
            }
        }
    }

    @GetMapping(value = SEARCH)
    fun search() : ResponseEntity<Any> =
        ResponseEntity.ok(Resources<Resource<SearchableAuction>>(searchableAuctionRepo.findAll().map {
            Resource<SearchableAuction>(it)
        }))

}