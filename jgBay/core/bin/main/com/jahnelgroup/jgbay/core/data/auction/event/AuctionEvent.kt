package com.jahnelgroup.jgbay.core.data.auction.event

import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.common.search.integration.event.SearchUpdateEvent
import org.springframework.context.ApplicationEvent

open class AuctionEvent(var auction: Auction) : ApplicationEvent(auction), SearchUpdateEvent