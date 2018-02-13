package com.jahnelgroup.jgbay.common.search.rest

import com.jahnelgroup.jgbay.common.AbstractTest
import com.jahnelgroup.jgbay.common.search.integration.RestEventsIntegrationConfig
import com.jahnelgroup.jgbay.common.search.integration.SearchServiceIntegrationConfig
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.rest.core.event.*
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [RestEventsIntegrationConfig::class, SearchServiceIntegrationConfig::class])
class RestEventFlowTests : AbstractTest() {

    @Autowired @Qualifier("repositoryEventsPubSubChannel") lateinit var pubSub: PublishSubscribeChannel

    @MockBean @Qualifier("searchCreateChannel") lateinit var searchCreateChannel: DirectChannel
    @MockBean @Qualifier("searchUpdateChannel") lateinit var searchUpdateChannel: DirectChannel
    @MockBean @Qualifier("searchDeleteChannel") lateinit var searchDeleteChannel: DirectChannel

    private fun verifyTimes(vararg pair: Pair<DirectChannel,Int>){
        pair.forEach {
            Mockito.verify(it.first, Mockito.times(it.second)).send(Mockito.any())
        }
    }

    @Before
    fun before(){
        arrayOf(searchCreateChannel, searchUpdateChannel, searchDeleteChannel).forEach {
            Mockito.`when`(it.send(Mockito.any())).then({true}) // just accept the message
        }
    }

    @Test
    fun `AfterCreateEvent to searchCreateChannel`(){
        // Searchable
        val createEvent = MessageBuilder.withPayload(AfterCreateEvent(SearchableEntity())).build()
        pubSub.send(createEvent)
        verifyTimes(Pair(searchCreateChannel,1), Pair(searchUpdateChannel,0), Pair(searchDeleteChannel,0))
    }

    @Test
    fun `AfterSaveEvent to searchUpdateChannel`(){
        val createEvent = MessageBuilder.withPayload(AfterSaveEvent(SearchableEntity())).build()
        pubSub.send(createEvent)
        verifyTimes(Pair(searchCreateChannel,0), Pair(searchUpdateChannel,1), Pair(searchDeleteChannel,0))
    }

    @Test
    fun `AfterDeleteEvent to searchDeleteChannel`(){
        val createEvent = MessageBuilder.withPayload(AfterDeleteEvent(SearchableEntity())).build()
        pubSub.send(createEvent)
        verifyTimes(Pair(searchCreateChannel,0), Pair(searchUpdateChannel,0), Pair(searchDeleteChannel,1))
    }

    @Test
    fun `All other Repository events to nullChannel`(){
        var e = NonSearchableEntity()
        arrayOf(BeforeCreateEvent(e), BeforeSaveEvent(e), BeforeDeleteEvent(e), AfterLinkSaveEvent(e, null), AfterLinkDeleteEvent(e, null)).forEach {
            verifyTimes(Pair(searchCreateChannel,0), Pair(searchUpdateChannel,0), Pair(searchDeleteChannel,0))
        }
    }

}