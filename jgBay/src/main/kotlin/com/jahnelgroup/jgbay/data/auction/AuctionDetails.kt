package com.jahnelgroup.jgbay.data.auction

import javax.persistence.Embeddable

@Embeddable
data class AuctionDetails (

        var description : String = "",
        var quantity : Long = 0

)