package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.AllergenDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class AllergenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test that checks if all 6 default allergens are present.
     * Logic from 'main' branch.
     */
    @Test
    public void testGetAllAllergens() throws Exception {
        
        mockMvc.perform(get("/api/allergens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[?(@.name=='Gluten')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.name=='Lactose')]", hasSize(1)));
    }

    /**
     * Test for creating a new allergen.
     * Logic from 'HEAD' branch adapted to integration test.
     */
    @Test
    public void testCreateAllergen() throws Exception {
        AllergenDTO request = new AllergenDTO(null, "Nueces", "Frutos de cáscara");

        mockMvc.perform(post("/api/allergens/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()); // Standard 201 Created

        // Verify it was added
        mockMvc.perform(get("/api/allergens"))
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$[?(@.name=='Nueces')]", hasSize(1)));
    }
}