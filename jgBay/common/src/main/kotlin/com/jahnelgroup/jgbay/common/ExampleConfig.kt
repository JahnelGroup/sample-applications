package com.jahnelgroup.jgbay.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExampleConfig {

    @Bean
    fun stringBean(): String {
        return "HELLO WORLD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    }

}
