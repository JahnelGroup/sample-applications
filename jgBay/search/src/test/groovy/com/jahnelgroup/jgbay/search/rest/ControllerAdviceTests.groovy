package com.jahnelgroup.jgbay.search.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.jahnelgroup.jgbay.search.data.document.DocumentService
import com.jahnelgroup.jgbay.search.data.index.IndexService
import org.elasticsearch.ElasticsearchStatusException
import org.elasticsearch.rest.RestStatus
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ControllerAdviceTests extends BaseSearchApplicationTests {

    @MockBean
    private IndexService indexService

    @MockBean
    private DocumentService documentService

    @Autowired
    private ObjectMapper objectMapper

    @Autowired
    private WebApplicationContext ctx

    private MockMvc mvc

    @Before
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx).build()
    }

    @Test
    void '[IndexService] Test ResponseException'() {
        when(indexService.createIndex(BDDMockito.any(String.class), BDDMockito.any(JsonNode.class)))
                .thenThrow(new ElasticsearchStatusException('Error message', RestStatus.CONFLICT))
        mvc.perform(post("/mappings/index")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString([key:'value']).getBytes())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath('$.status', Matchers.is(RestStatus.CONFLICT.name())))
                .andExpect(jsonPath('$.message', Matchers.is('Error message')))
    }

    @Test
    void '[DocumentService] Test ResponseException'() {
        when(documentService.index(BDDMockito.any(String.class), BDDMockito.any(String.class), BDDMockito.any(JsonNode.class)))
                .thenThrow(new ElasticsearchStatusException('Error message', RestStatus.CONFLICT))
        mvc.perform(post("/index/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString([key:'value']).getBytes())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath('$.status', Matchers.is(RestStatus.CONFLICT.name())))
                .andExpect(jsonPath('$.message', Matchers.is('Error message')))
    }

}
