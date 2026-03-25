package com.justorder.backend.controller;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.DishDTO;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class DishControllerTest {

        private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testDishLifecycle() throws Exception {
        DishDTO newDish = new DishDTO(null, "Test Dish", "Test Description", 10.5, 1L, null);
        ArrayList<String> allergenNames = new ArrayList<>();
        allergenNames.add("Gluten");
        newDish.setAllergenNames(allergenNames);

        MvcResult result = mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDish)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        DishDTO createdDish = objectMapper.readValue(responseContent, DishDTO.class);
        Long dishId = createdDish.getId();

        createdDish.setPrice(12.5);
        allergenNames = new ArrayList<>();
        if(createdDish.getAllergenNames() != null) {
                for (String allergenName : createdDish.getAllergenNames()) {
                        allergenNames.add(allergenName);
                }
        }
        allergenNames.add("Lactose");
        createdDish.setAllergenNames(allergenNames);
        createdDish.setDescription("Updated Description");
        mockMvc.perform(put("/api/dishes/" + dishId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdDish)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/dishes/" + dishId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateDishWithMismatchedRestaurantId() throws Exception {
        DishDTO newDish = new DishDTO(null, "Test Dish", "Test", 10.0, 2L, null);

        mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDish)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDishWithInvalidData() throws Exception {
        DishDTO badDishMissingName = new DishDTO(null, "", "Description", 10.5, 1L, null);
        mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badDishMissingName)))
                .andExpect(status().isBadRequest());
        
        DishDTO badDishMissingdesc = new DishDTO(null, "Name", "", 10.5, 1L, null);
        mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badDishMissingdesc)))
                .andExpect(status().isBadRequest());

        DishDTO badDishNegativePrice = new DishDTO(null, "Name", "Description", -5.0, 1L, null);
        mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badDishNegativePrice)))
                .andExpect(status().isBadRequest());

        DishDTO badDishUnknownAllergen = new DishDTO(null, "Name", "Desc", 10.0, 1L, null);
        badDishUnknownAllergen.setAllergenNames(Arrays.asList("NonExistentAllergen"));
        mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badDishUnknownAllergen)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateDishRestaurantNotFound() throws Exception {
        DishDTO newDish = new DishDTO(null, "Test Dish", "Desc", 10.0, 9999L, null);

        mockMvc.perform(post("/api/dishes/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDish)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateDishNotFound() throws Exception {
        DishDTO updateDish = new DishDTO(null, "Test Dish", "Desc", 10.0, 1L, null);

        mockMvc.perform(put("/api/dishes/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDish)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateDishWithInvalidData() throws Exception {
        DishDTO newDish = new DishDTO(null, "Initial Name", "Initial Desc", 10.0, 1L, null);
        MvcResult result = mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDish)))
                .andExpect(status().isCreated())
                .andReturn();

        DishDTO createdDish = objectMapper.readValue(result.getResponse().getContentAsString(), DishDTO.class);

        createdDish.setDescription("");
        mockMvc.perform(put("/api/dishes/" + createdDish.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdDish)))
                .andExpect(status().isBadRequest());

        createdDish.setDescription("Updated");
        createdDish.setAllergenNames(Arrays.asList("UnknownAllergen"));
        mockMvc.perform(put("/api/dishes/" + createdDish.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdDish)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteDishNotFound() throws Exception {
        mockMvc.perform(delete("/api/dishes/99999"))
                .andExpect(status().isNotFound());
    }
}
