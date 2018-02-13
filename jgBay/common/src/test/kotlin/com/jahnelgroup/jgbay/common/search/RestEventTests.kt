package com.jahnelgroup.jgbay.common.search

import com.jahnelgroup.jgbay.common.AbstractTest
import com.jahnelgroup.jgbay.common.search.integration.RestEventsIntegrationConfig
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.event.BeforeCreateEvent
import org.springframework.data.rest.core.event.RepositoryEvent
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.MessageChannel
import org.springframework.test.context.ContextConfiguration

@Ignore
@ContextConfiguration(classes = arrayOf(RestEventsIntegrationConfig::class, RestEventTestsConfig::class))
class RestEventTests : AbstractTest() {

    @Autowired
    lateinit var repositoryEventsPubSubChannel: MessageChannel

    @Autowired
    lateinit var repositoryEventsPubSubChannelTest: QueueChannel

    @Test
    fun `my test`(){
        var inMessage = MessageBuilder.withPayload<RepositoryEvent>(BeforeCreateEvent("test")).build()
        repositoryEventsPubSubChannel.send(inMessage)

        var outMessage = repositoryEventsPubSubChannelTest.receive(0)
        Assert.assertNotNull(outMessage)
    }

}