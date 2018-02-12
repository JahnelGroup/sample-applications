package com.jahnelgroup.jgbay.core.data.auction.event

import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.data.auction.bid.Bid

open class BidAddedEvent(auction: Auction, var bid: Bid): AuctionEvent(auction)