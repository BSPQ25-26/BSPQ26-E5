package com.justorder.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetMenu() throws Exception {
        mockMvc.perform(get("/api/restaurants/1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[?(@.name=='Plato 1' && @.description=='Plato 1 descripcion' && @.price==23.0)]", hasSize(1)))
            .andExpect(jsonPath("$[?(@.name=='Plato 2' && @.description=='Plato 2 descripcion' && @.price==25.0)]", hasSize(1)));
    }
}
