package com.jahnelgroup.jgbay.data.auction.category

import com.jahnelgroup.jgbay.data.AbstractEntity
import javax.persistence.Entity

@Entity
class Category(
    var name : String = ""
) : AbstractEntity()