package com.jahnelgroup.jgbay.common

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
abstract class AbstractTest {

    class NonSearchable

    @Autowired
    lateinit var appEventPublisher: ApplicationEventPublisher

}