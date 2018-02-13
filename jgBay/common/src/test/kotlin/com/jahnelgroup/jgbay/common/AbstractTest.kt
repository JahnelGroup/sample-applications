package com.jahnelgroup.jgbay.common

import com.jahnelgroup.jgbay.common.search.Searchable
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.integration.config.EnableIntegration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@EnableIntegration
@RunWith(SpringJUnit4ClassRunner::class)
abstract class AbstractTest {

    class NonSearchableEntity

    @Searchable("index", transformRef = "na")
    class SearchableEntity

    @Autowired
    lateinit var appEventPublisher: ApplicationEventPublisher

}