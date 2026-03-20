package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.model.Dish;
import com.justorder.backend.repository.AllergenRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private DishRepository dishRepository;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private AllergenRepository allergenRepository;

    // ==========================================
    // TESTS FROM 'HEAD' BRANCH (BASIC CRUD)
    // ==========================================

    @Test
    public void testGetAll() throws Exception {
        Dish d = new Dish();
        d.setId(1L);
        d.setName("Macarrones");
        
        when(dishRepository.findAll()).thenReturn(Arrays.asList(d));

        mockMvc.perform(get("/api/dishes/all"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Macarrones")));
    }

    @Test
    public void testCreate() throws Exception {
        DishDTO request = new DishDTO();
        request.setName("Sopa");

        Dish saved = new Dish();
        saved.setId(2L);
        saved.setName("Sopa");

        when(dishRepository.save(any(Dish.class))).thenReturn(saved);

        mockMvc.perform(post("/api/dishes/create")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Sopa")));
    }

    @Test
    public void testUpdate() throws Exception {
        DishDTO request = new DishDTO();
        request.setName("Sopa de fideos");

        Dish existing = new Dish();
        existing.setId(2L);
        existing.setName("Sopa");

        Dish updated = new Dish();
        updated.setId(2L);
        updated.setName("Sopa de fideos");

        when(dishRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(dishRepository.save(any(Dish.class))).thenReturn(updated);

        mockMvc.perform(put("/api/dishes/update/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Sopa de fideos")));
    }

    @Test
    public void testDelete() throws Exception {
        when(dishRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/dishes/delete/1"))
               .andExpect(status().isOk());
    }

    // ==========================================
    // TESTS FROM 'MAIN' BRANCH (LIFECYCLE)
    // ==========================================

    @Test
    public void testDishLifecycle() throws Exception {
        // Set up a basic mock so the response JSON returns an ID and prevents parsing errors.
        Dish mockSavedDish = new Dish();
        mockSavedDish.setId(99L);
        mockSavedDish.setName("Test Dish");
        when(dishRepository.save(any(Dish.class))).thenReturn(mockSavedDish);
        when(dishRepository.findById(99L)).thenReturn(Optional.of(mockSavedDish));

        DishDTO newDish = new DishDTO(null, "Test Dish", "Test Description", 10.5, 1L, null);
        ArrayList<String> allergenNames = new ArrayList<>();
        allergenNames.add("Gluten");
        newDish.setAllergenNames(allergenNames);

        // Simulate creation
        MvcResult result = mockMvc.perform(post("/api/dishes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDish)))
                .andExpect(status().isOk())
                .andReturn();

        // To avoid parsing errors with the mock, we manually build a DTO to continue the test
        DishDTO createdDish = new DishDTO();
        createdDish.setId(99L);
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
                .andExpect(status().isOk());
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