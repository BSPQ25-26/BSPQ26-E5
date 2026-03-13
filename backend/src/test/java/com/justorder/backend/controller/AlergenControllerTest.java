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
public class AlergenControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetAllAlergens() throws Exception {
        
        mockMvc.perform(get("/api/alergens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name=='Gluten' && @.description=='Cereales con gluten')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.name=='Lactosa' && @.description=='Productos lacteos')]", hasSize(1)));
    }
}
