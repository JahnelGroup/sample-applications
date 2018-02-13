package com.jahnelgroup.jgbay.common

import com.jahnelgroup.jgbay.common.search.Searchable
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.integration.config.EnableIntegration
import org.springframework.messaging.MessageChannel
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@EnableIntegration
@RunWith(SpringJUnit4ClassRunner::class)
abstract class AbstractTest {

    @Searchable("index", transformRef = "na")
    class SearchableEntity

    @Autowired
    lateinit var appEventPublisher: ApplicationEventPublisher

    fun mockChannelAccept(channel: MessageChannel){
        Mockito.`when`(channel.send(Mockito.any())).then({true}) // just accept the message
    }

    fun verifyChannelCount(vararg pair: Pair<MessageChannel,Int>){
        pair.forEach {
            Mockito.verify(it.first, Mockito.times(it.second)).send(Mockito.any())
        }
    }

}