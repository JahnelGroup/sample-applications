package com.jahnelgroup.jgbay.common.search

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.channel.MessageChannels

@Configuration
class RestEventTestsConfig{

    @Bean
    fun repositoryEventsPubSubChannelTest() = MessageChannels.queue("repositoryEventsPubSubChannelTest")

    @Bean
    fun repositoryEventsPubSubChannelTestFlow(): IntegrationFlow {
        return IntegrationFlows.from("repositoryEventsPubSubChannel")
                .channel(repositoryEventsPubSubChannelTest()).get()
    }

}