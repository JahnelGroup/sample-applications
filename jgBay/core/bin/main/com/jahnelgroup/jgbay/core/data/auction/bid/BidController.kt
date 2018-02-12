package com.jahnelgroup.jgbay.core.data.auction.bid

import com.jahnelgroup.jgbay.core.context.UserContextService
import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.data.auction.AuctionRepo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.hateoas.Resource
import org.springframework.hateoas.ResourceProcessor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

const val MY_BID_LINK : String = "myBid"
const val SUBMIT_BID_LINK : String = "submitBid"
const val CANCEL_BID_LINK : String = "cancelBid"

/**
 * Handles bidding requests.
 */
@Configuration
@RestController
@RequestMapping("/api/auctions/{id}")
class BidController(
        private val userContextService: UserContextService,
        private val entityLinks: RepositoryEntityLinks,
        private val auctionRepo: AuctionRepo
) {

    @Bean
    fun auctionLinks() = ResourceProcessor<Resource<Auction>> {
        it.apply {
            if( it.content.isAcceptingBids() ){
                var myBid = it.content.getBidByUser(userContextService.getCurrentUserId()!!)

                if( myBid != null ){
                    it.add(entityLinks.linkForSingleResource(myBid).withRel(MY_BID_LINK))
                    it.add(entityLinks.linkForSingleResource(it.content).slash(CANCEL_BID_LINK).withRel(CANCEL_BID_LINK))
                }else{
                    it.add(entityLinks.linkForSingleResource(it.content).slash(SUBMIT_BID_LINK).withRel(SUBMIT_BID_LINK))
                }
            }

        }
    }

    /**
     * Submit a Bid
     *
     * TODO: This doesn't trigger the upsertFlow Integration flow for an Auction...
     */
    @PostMapping(SUBMIT_BID_LINK)
    fun submitBid(@PathVariable("id") auction: Auction?, @RequestBody bid: Bid) : Bid {
        var currentUserId = userContextService.getCurrentUserId()!!

        if (auction == null || auction.id == null) {
            throw AuctionNotFoundException()
        }

        if (!auction.isAcceptingBids()) {
            throw UnableToBidException()
        }

        var existingBid = auction.getBidByUser(currentUserId)
        return if( existingBid != null ){
            auction.updateBid(existingBid, bid)
            auctionRepo.save(auction)
            existingBid
        }else {
            bid.createdBy = currentUserId
            auction.addBid(bid)
            auctionRepo.save(auction).getBidByUser(currentUserId)!!
        }
    }

    /**
     * Cancel a Bid
     */
    @DeleteMapping(CANCEL_BID_LINK)
    fun cancelBId(@PathVariable("id") auction: Auction?) : ResponseEntity<Any> {
        if (auction != null && auction.id != null) {
            var bid = auction.getBidByUser(userContextService.getCurrentUserId()!!)
            if( bid != null ){
                auction.removeBid(bid)
                auctionRepo.save(auction)
            }
        }
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
class UnableToBidException : RuntimeException()

@ResponseStatus(HttpStatus.NOT_FOUND)
class AuctionNotFoundException : RuntimeException()