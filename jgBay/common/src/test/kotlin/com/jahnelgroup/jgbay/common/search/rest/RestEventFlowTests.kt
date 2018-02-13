package com.jahnelgroup.jgbay.common.search.rest

import com.jahnelgroup.jgbay.common.AbstractTest
import com.jahnelgroup.jgbay.common.search.integration.RestEventsIntegrationConfig
import com.jahnelgroup.jgbay.common.search.integration.SearchChannelsIntegrationConfig
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.rest.core.event.*
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [RestEventsIntegrationConfig::class, SearchChannelsIntegrationConfig::class])
class RestEventFlowTests : AbstractTest() {

    @Autowired @Qualifier("repositoryEventsPubSubChannel") lateinit var pubSub: PublishSubscribeChannel

    @MockBean @Qualifier("searchCreateChannel") lateinit var searchCreateChannel: DirectChannel
    @MockBean @Qualifier("searchUpdateChannel") lateinit var searchUpdateChannel: DirectChannel
    @MockBean @Qualifier("searchDeleteChannel") lateinit var searchDeleteChannel: DirectChannel

    @Before
    fun before(){
        arrayOf(searchCreateChannel, searchUpdateChannel, searchDeleteChannel).forEach {
            mockChannelAccept(it)
        }
    }

    @Test
    fun `AfterCreateEvent to searchCreateChannel`(){
        pubSub.send(MessageBuilder.withPayload(AfterCreateEvent(SearchableEntity())).build())
        verifyChannelCount(
                Pair(searchCreateChannel,1),
                Pair(searchUpdateChannel,0),
                Pair(searchDeleteChannel,0))
    }

    @Test
    fun `AfterSaveEvent to searchUpdateChannel`(){
        pubSub.send(MessageBuilder.withPayload(AfterSaveEvent(SearchableEntity())).build())
        verifyChannelCount(
                Pair(searchCreateChannel,0),
                Pair(searchUpdateChannel,1),
                Pair(searchDeleteChannel,0))
    }

    @Test
    fun `AfterDeleteEvent to searchDeleteChannel`(){
        pubSub.send(MessageBuilder.withPayload(AfterDeleteEvent(SearchableEntity())).build())
        verifyChannelCount(
                Pair(searchCreateChannel,0),
                Pair(searchUpdateChannel,0),
                Pair(searchDeleteChannel,1))
    }

    @Test
    fun `All other Repository events to nullChannel`(){
        arrayOf(BeforeCreateEvent({}), BeforeSaveEvent({}), BeforeDeleteEvent({}), AfterLinkSaveEvent({}, null), AfterLinkDeleteEvent({}, null)).forEach {
            verifyChannelCount(
                    Pair(searchCreateChannel,0),
                    Pair(searchUpdateChannel,0),
                    Pair(searchDeleteChannel,0))
        }
    }

}