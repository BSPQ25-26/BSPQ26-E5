package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.DishDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class DishControllerTest {

        private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testDishLifecycle() throws Exception {
        DishDTO newDish = new DishDTO(null, "Test Dish", "Test Description", 10.5, 1L, null);
        ArrayList<String> alergenNames = new ArrayList<>();
        alergenNames.add("Gluten");
        newDish.setAlergenNames(alergenNames);

        MvcResult result = mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDish)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        DishDTO createdDish = objectMapper.readValue(responseContent, DishDTO.class);
        Long dishId = createdDish.getId();

        createdDish.setPrice(12.5);
        alergenNames = new ArrayList<>();
        if(createdDish.getAlergenNames() != null) {
                for (String alergenName : createdDish.getAlergenNames()) {
                        alergenNames.add(alergenName);
                }
        }
        alergenNames.add("Lactose");
        createdDish.setAlergenNames(alergenNames);
        createdDish.setDescription("Updated Description");
        mockMvc.perform(put("/api/dishes/" + dishId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdDish)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/dishes/" + dishId))
                .andExpect(status().isNoContent());
    }
}
