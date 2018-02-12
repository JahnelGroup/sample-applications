package com.jahnelgroup.jgbay.common.rest

import com.jahnelgroup.jgbay.common.AbstractTest
import com.jahnelgroup.jgbay.common.search.integration.RestEventsIntegrationConfig
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.event.AfterCreateEvent
import org.springframework.integration.channel.QueueChannel
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = arrayOf(RestEventsIntegrationConfig::class, RestEventTestsConfig::class))
class RestEventTests : AbstractTest() {

    @Autowired
    lateinit var repositoryEventsPubSubChannelTest: QueueChannel

    @Test
    fun `non searcable`(){
        var createdEvent = AfterCreateEvent(NonSearchable())
        appEventPublisher.publishEvent(createdEvent)

        var event = repositoryEventsPubSubChannelTest.receive(0)
        Assert.assertNotNull(event)
    }

}