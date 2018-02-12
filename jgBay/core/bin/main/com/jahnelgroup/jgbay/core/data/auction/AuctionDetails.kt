package com.jahnelgroup.jgbay.core.data.auction

import javax.persistence.Embeddable

@Embeddable
data class AuctionDetails (

        var description : String = "",
        var quantity : Long = 0

)