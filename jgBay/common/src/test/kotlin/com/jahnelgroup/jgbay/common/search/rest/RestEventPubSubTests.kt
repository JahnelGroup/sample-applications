package com.jahnelgroup.jgbay.common.search.rest

import com.jahnelgroup.jgbay.common.AbstractTest
import com.jahnelgroup.jgbay.common.search.integration.RestEventsIntegrationConfig
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.rest.core.event.*
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [RestEventsIntegrationConfig::class])
class RestEventPubSubTests : AbstractTest() {

    @MockBean
    @Qualifier("repositoryEventsPubSubChannel") lateinit var pubSub: PublishSubscribeChannel

    @Before
    fun before(){
        mockChannelAccept(pubSub)
    }

    // pretend to be any repo event
    class RepoEvent(any: Any) : RepositoryEvent(any)

    @Test
    fun `RepositoryEvent should be published`(){
        appEventPublisher.publishEvent(RepoEvent({}))
        verifyChannelCount(Pair(pubSub, 1))
    }

    @Test
    fun `non repo events should not be published`(){
        appEventPublisher.publishEvent(object{})
        verifyChannelCount(Pair(pubSub, 0))
    }

}