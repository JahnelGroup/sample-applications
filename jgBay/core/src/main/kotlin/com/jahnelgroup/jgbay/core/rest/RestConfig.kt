package com.jahnelgroup.jgbay.core.rest

import com.jahnelgroup.jgbay.core.data.auction.Auction
import com.jahnelgroup.jgbay.core.data.auction.bid.Bid
import com.jahnelgroup.jgbay.core.data.user.User
import com.jahnelgroup.jgbay.core.search.auction.SearchableAuction
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter


@Configuration
class RestConfig : RepositoryRestConfigurerAdapter() {

    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(
                Auction::class.java,
                SearchableAuction::class.java,
                Bid::class.java,
                User::class.java
        )

        config.setBasePath("api")
    }

}