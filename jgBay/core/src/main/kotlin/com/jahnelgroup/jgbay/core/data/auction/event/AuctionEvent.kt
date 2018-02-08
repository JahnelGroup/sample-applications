package com.jahnelgroup.jgbay.core.data.auction.event

import com.jahnelgroup.jgbay.core.data.auction.Auction
import org.springframework.context.ApplicationEvent

open class AuctionEvent(var auction: Auction) : ApplicationEvent(auction)