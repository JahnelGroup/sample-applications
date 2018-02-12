package com.jahnelgroup.jgbay.common

import com.jahnelgroup.jgbay.common.search.integration.RestEventsIntegrationConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer
import org.springframework.integration.support.MessageBuilder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@ContextConfiguration(classes = arrayOf(RestEventsIntegrationConfig::class))
@RunWith(SpringJUnit4ClassRunner::class)
class RestEventTests {

    class NonSearchable

    @Autowired
    lateinit var repositoryEventsPubSubChannel: ApplicationEventListeningMessageProducer

    @Test
    fun `non searcables`(){
        var created = AfterCreateEvent(NonSearchable())
        val message = MessageBuilder.withPayload(created).build()

    }

}