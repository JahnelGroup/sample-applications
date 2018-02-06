package com.jahnelgroup.jgbay.search.rest

import com.jahnelgroup.jgbay.search.data.auction.Auction
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter

@Configuration
class RestConfig : RepositoryRestConfigurerAdapter() {

    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(
                Auction::class.java
        )

        config.setBasePath("api")
    }

}