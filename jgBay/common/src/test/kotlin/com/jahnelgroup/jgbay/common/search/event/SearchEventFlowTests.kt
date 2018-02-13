package com.jahnelgroup.jgbay.common.search.event

import com.jahnelgroup.jgbay.common.AbstractTest
import com.jahnelgroup.jgbay.common.search.integration.RestEventsIntegrationConfig
import com.jahnelgroup.jgbay.common.search.integration.SearchEventsIntegrationConfig
import com.jahnelgroup.jgbay.common.search.integration.SearchServiceIntegrationConfig
import com.jahnelgroup.jgbay.common.search.integration.event.SearchCreateEvent
import com.jahnelgroup.jgbay.common.search.integration.event.SearchDeleteEvent
import com.jahnelgroup.jgbay.common.search.integration.event.SearchUpdateEvent
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationEvent
import org.springframework.data.rest.core.event.*
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SearchEventsIntegrationConfig::class, SearchServiceIntegrationConfig::class])
class SearchEventFlowTests : AbstractTest() {

    @Autowired @Qualifier("searchEventsPubSubChannel") lateinit var pubSub: PublishSubscribeChannel

    @MockBean @Qualifier("searchCreateChannel") lateinit var searchCreateChannel: DirectChannel
    @MockBean @Qualifier("searchUpdateChannel") lateinit var searchUpdateChannel: DirectChannel
    @MockBean @Qualifier("searchDeleteChannel") lateinit var searchDeleteChannel: DirectChannel

    open class TestCreate(any: Any) : ApplicationEvent(any), SearchCreateEvent
    open class TestUpdate(any: Any) : ApplicationEvent(any), SearchUpdateEvent
    open class TestDelete(any: Any) : ApplicationEvent(any), SearchDeleteEvent

    @Before
    fun before(){
        arrayOf(searchCreateChannel, searchUpdateChannel, searchDeleteChannel).forEach {
            mockChannelAccept(it)
        }
    }

    @Test
    fun `SearchCreateEvent to searchCreateChannel`(){
        pubSub.send(MessageBuilder.withPayload(TestCreate(SearchableEntity())).build())
        verifyChannelCount(
                Pair(searchCreateChannel,1),
                Pair(searchUpdateChannel,0),
                Pair(searchDeleteChannel,0))
    }

    @Test
    fun `SearchUpdateEvent to searchUpdateChannel`(){
        pubSub.send(MessageBuilder.withPayload(TestUpdate(SearchableEntity())).build())
        verifyChannelCount(
                Pair(searchCreateChannel,0),
                Pair(searchUpdateChannel,1),
                Pair(searchDeleteChannel,0))
    }

    @Test
    fun `SearchDeleteEvent to searchDeleteChannel`(){
        pubSub.send(MessageBuilder.withPayload(TestDelete(SearchableEntity())).build())
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