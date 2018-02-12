package com.jahnelgroup.jgbay.core.user

import com.jahnelgroup.jgbay.core.AbstractTest
import org.hamcrest.Matchers
import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

class UserTests : AbstractTest() {

    @Test
    fun `raw user list`() {
        mockMvc.perform(get("/api/users").with(STEVEN_CREDENTIALS))
            .andExpect(jsonPath("$._embedded.users[*].username",
                    Matchers.contains("steven", "carrie", "lauren")))
    }

}