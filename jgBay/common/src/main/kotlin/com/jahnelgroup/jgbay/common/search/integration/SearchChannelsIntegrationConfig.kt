package com.jahnelgroup.jgbay.common.search.integration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.channel.MessageChannels

@Configuration
class SearchChannelsIntegrationConfig {

    @Bean fun searchCreateChannel(): DirectChannel = MessageChannels.direct("searchCreateChannel").get()
    @Bean fun searchUpdateChannel(): DirectChannel = MessageChannels.direct("searchUpdateChannel").get()
    @Bean fun searchDeleteChannel(): DirectChannel = MessageChannels.direct("searchDeleteChannel").get()

    @Bean
    fun searchCreateChannelFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchCreateChannel())
                .enrichHeaders({it.header("searchAction", "create")})
                .channel("searchInboundGatewayChannel")
                .get()
    }

    @Bean
    fun searchUpdateChannelFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchUpdateChannel())
                .enrichHeaders({it.header("searchAction", "update")})
                .channel("searchInboundGatewayChannel")
                .get()
    }

    @Bean
    fun searchDeleteChannelFlow(): IntegrationFlow {
        return IntegrationFlows.from(searchDeleteChannel())
                .enrichHeaders({it.header("searchAction", "delete")})
                .channel("searchInboundGatewayChannel")
                .get()
    }

}