package com.jahnelgroup.jgbay.core.data.user.search

import com.jahnelgroup.jgbay.common.search.SearchableTransformer
import com.jahnelgroup.jgbay.core.data.user.User
import org.springframework.stereotype.Component

@Component("userSearchTransformer")
class UserSearchTransformer : SearchableTransformer<User> {

    override fun from(from: User): Any =
            object {
                var id : Long        = from.id!!
                var username: String = from.username
                var rating: Short    = from.rating
            }

}