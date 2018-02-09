package com.jahnelgroup.jgbay.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.jahnelgroup.jgbay.core.App
import com.jahnelgroup.jgbay.core.context.SpringSecurityUserContext
import com.jahnelgroup.jgbay.core.context.UserContextService
import com.jahnelgroup.jgbay.core.data.user.User
import com.jahnelgroup.jgbay.core.data.user.UserRepo
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import java.io.UnsupportedEncodingException
import org.springframework.test.web.servlet.MvcResult

@SpringBootTest(classes = arrayOf(App::class, AbstractTest.Companion.TestConfig::class))
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
abstract class AbstractTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    companion object {
        @Configuration
        class TestConfig {
            @Primary
            @Bean
            fun testable(userRepo: UserRepo) = object : SpringSecurityUserContext(userRepo) {
                override fun getCurrentUserId(): Long? = userRepo.findByUsername(getCurrentUsername()!!).get().id
            }

        }
    }

    var STEVEN_CREDENTIALS = httpBasic("steven", "password")
    var CARRIE_CREDENTIALS = httpBasic("carrie", "password")
    var LAUREN_CREDENTIALS = httpBasic("lauren", "password")

    @Throws(UnsupportedEncodingException::class)
    fun contentBody(result: MvcResult): String {
        return result.response.contentAsString
    }
}