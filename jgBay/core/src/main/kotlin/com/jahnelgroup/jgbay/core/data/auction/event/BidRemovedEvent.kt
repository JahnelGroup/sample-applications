package com.jahnelgroup.jgbay.core.data.auction.event

import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.data.auction.bid.Bid

class BidRemovedEvent(auction: Auction, var bid: Bid): AuctionEvent(auction)