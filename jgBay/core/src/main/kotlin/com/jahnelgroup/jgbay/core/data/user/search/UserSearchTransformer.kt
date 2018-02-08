package com.jahnelgroup.jgbay.core.data.user.search

import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.data.user.User
import com.jahnelgroup.jgbay.core.search.SearchableTransformer
import org.springframework.stereotype.Component

@Component("userSearchTransformer")
class UserSearchTransformer : SearchableTransformer<Auction, UserSearchTransformer.Companion.SearchableUser> {

    companion object {
        data class SearchableUser(
                var id : Long = 0,
                var username: String = "",
                var rating: Short = 0
        )
    }

    override fun from(from: Any): SearchableUser =
            with(from as User){
                SearchableUser().apply {
                    this.id = from.id!!
                    this.username = from.username
                    this.rating = from.rating
                }
            }
}