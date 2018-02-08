package com.jahnelgroup.jgbay.core.auction

import com.jahnelgroup.jgbay.core.AbstractTest
import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.data.auction.AuctionDetails
import com.jahnelgroup.jgbay.core.data.user.UserRepo
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class AuctionTests : AbstractTest() {

    @Autowired
    lateinit var userRepo : UserRepo

    @Test
    fun `create an auction`() {
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/auctions").with(STEVEN_CREDENTIALS)
            .content(objectMapper.writeValueAsBytes(Auction(
                    title = "Intel CPU's",
                    auctionDetails = AuctionDetails(
                            description = "Selling brand new intel CPU's, trusted seller!",
                            quantity = 10)
            ))))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn()

//        mockMvc.perform(MockMvcRequestBuilders.get("/users/"+stevenId+"/sellingAuctions").with(STEVEN_CREDENTIALS))
//                .andExpect(MockMvcResultMatchers.status().isOk)
    }

}