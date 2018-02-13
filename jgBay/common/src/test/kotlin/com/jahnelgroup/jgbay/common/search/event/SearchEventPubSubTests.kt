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
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationEvent
import org.springframework.data.rest.core.event.BeforeCreateEvent
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.messaging.MessageChannel
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SearchEventsIntegrationConfig::class])
class SearchEventPubSubTests : AbstractTest() {

    @MockBean
    @Qualifier("searchEventsPubSubChannel") lateinit var pubSub: PublishSubscribeChannel

    open class TestCreate(any: Any) : ApplicationEvent(any), SearchCreateEvent
    open class TestUpdate(any: Any) : ApplicationEvent(any), SearchUpdateEvent
    open class TestDelete(any: Any) : ApplicationEvent(any), SearchDeleteEvent

    @Before
    fun before(){
        mockChannelAccept(pubSub)
    }

    @Test
    fun `search events should be published`(){
        arrayOf(TestCreate(SearchableEntity()), TestUpdate(SearchableEntity()), TestDelete(SearchableEntity())).forEach {
            appEventPublisher.publishEvent(it) // implements marker interface
        }
        verifyChannelCount(Pair(pubSub, 3))
    }

    @Test
    fun `non search events should not be published`(){
        appEventPublisher.publishEvent(object{})
        verifyChannelCount(Pair(pubSub, 0))
    }

}