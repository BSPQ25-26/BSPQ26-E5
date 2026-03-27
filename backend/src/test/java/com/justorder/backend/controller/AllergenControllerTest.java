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
public class AllergenControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetAllAllergens() throws Exception {
        
        mockMvc.perform(get("/api/allergens"))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name=='Gluten' && @.description=='Cereals containing gluten')]", hasSize(1)))
            .andExpect(jsonPath("$[?(@.name=='Lactose' && @.description=='Milk and dairy products')]", hasSize(1)))
            .andExpect(jsonPath("$[?(@.name=='Peanuts' && @.description=='Peanuts and peanut-based products')]", hasSize(1)))
            .andExpect(jsonPath("$[?(@.name=='Shellfish' && @.description=='Crustaceans and shellfish products')]", hasSize(1)))
            .andExpect(jsonPath("$[?(@.name=='Soy' && @.description=='Soybeans and soy-based products')]", hasSize(1)))
            .andExpect(jsonPath("$[?(@.name=='Eggs' && @.description=='Eggs and egg-based products')]", hasSize(1)));
    }
}
