package com.jahnelgroup.jgbay.core

import org.springframework.context.ApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MyEventListener {

    @EventListener
    fun listn(applicationEvent: ApplicationEvent){
        println("Event: ${applicationEvent}")
    }

}