package com.jahnelgroup.jgbay.core.data.auction.event

import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.data.auction.bid.Bid

class BidUpdatedEvent(auction: Auction, var bid: Bid, var incoming: Bid): AuctionEvent(auction)