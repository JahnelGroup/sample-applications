package com.jahnelgroup.jgbay.core

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@SpringBootApplication
class App

fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, *args)
}

@Component
class ExampleConfigTester(
    val stringBean: String
) {

    companion object {
        var logger = LoggerFactory.getLogger(ExampleConfigTester::class.java)
    }

    @PostConstruct
    fun printMessage() {
        logger.info(stringBean)
    }
}
