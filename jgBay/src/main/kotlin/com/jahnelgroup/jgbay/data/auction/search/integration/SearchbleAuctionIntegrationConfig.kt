package com.jahnelgroup.jgbay.data.auction.search.integration

import com.jahnelgroup.jgbay.data.auction.Auction
import com.jahnelgroup.jgbay.data.user.UserRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.endpoint.MessageProducerSupport

@Configuration
open class SearchbleAuctionIntegrationConfig {

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var createAndSaveEvents: MessageProducerSupport

    @Bean
    open fun createFlow(): IntegrationFlow {
        return IntegrationFlows.from(createAndSaveEvents)
                .filter{event: RepositoryEvent -> event is AfterCreateEvent && event.source is Auction}
                .log()
                .handle { payload: AfterCreateEvent, _ ->
                    var auction = payload.source
                    println("Auction $auction")
//                    val elasticPerson = ElasticPerson()
//                    BeanUtils.copyProperties(payload.source as Person, elasticPerson)
//                    println(personSearchRepo.save(elasticPerson))
                }
                .get()

    }

}