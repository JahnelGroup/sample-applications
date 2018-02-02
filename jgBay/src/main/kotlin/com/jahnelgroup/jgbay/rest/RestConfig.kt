package com.jahnelgroup.jgbay.rest

import com.jahnelgroup.jgbay.data.auction.Auction
import com.jahnelgroup.jgbay.data.auction.bid.Bid
import com.jahnelgroup.jgbay.data.user.User
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter


@Configuration
class RestConfig : RepositoryRestConfigurerAdapter() {

    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(
                Auction::class.java,
                Bid::class.java,
                User::class.java
        )

        config.setBasePath("api")
    }

}