package com.jahnelgroup.jgbay.search

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import java.io.UnsupportedEncodingException

@SpringBootTest(classes = arrayOf(App::class))
@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
abstract class AbstractTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Throws(UnsupportedEncodingException::class)
    fun contentBody(result: MvcResult): String {
        return result.response.contentAsString
    }
}