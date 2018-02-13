package com.jahnelgroup.jgbay.common.search

import com.jahnelgroup.jgbay.common.AbstractTest
import com.jahnelgroup.jgbay.common.search.integration.RestEventsIntegrationConfig
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.rest.core.event.BeforeCreateEvent
import org.springframework.messaging.MessageChannel
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [(RestEventsIntegrationConfig::class)])
class RestEventTests : AbstractTest() {

    @MockBean
    @Qualifier("repositoryEventsPubSubChannel")
    lateinit var pubSub: MessageChannel

    @Test
    fun `RepositoryEvent should be published`(){
        Mockito.`when`(pubSub.send(Mockito.any())).then({true}) // just accept the message
        appEventPublisher.publishEvent(BeforeCreateEvent("anything"))
        Mockito.verify(pubSub, Mockito.times(1)).send(Mockito.any())
    }

    @Test
    fun `non repo events should not be published`(){
        Mockito.`when`(pubSub.send(Mockito.any())).then({true}) // just accept the message
        appEventPublisher.publishEvent(object{})
        Mockito.verify(pubSub, Mockito.times(0)).send(Mockito.any())
    }

}