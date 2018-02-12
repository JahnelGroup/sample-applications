package com.jahnelgroup.jgbay.common.search.integration.visualization

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.http.config.EnableIntegrationGraphController
import org.springframework.integration.support.management.graph.IntegrationGraphServer

@EnableIntegrationGraphController
@Configuration
class SpringIntegrationVisualizationConfig {

    @Bean
    fun integrationGraphServer() = IntegrationGraphServer()

}