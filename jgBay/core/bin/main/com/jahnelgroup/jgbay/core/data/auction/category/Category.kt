package com.jahnelgroup.jgbay.core.data.auction.category

import com.jahnelgroup.jgbay.core.data.AbstractEntity
import javax.persistence.Entity

@Entity
class Category(
    var name : String = ""
) : AbstractEntity()