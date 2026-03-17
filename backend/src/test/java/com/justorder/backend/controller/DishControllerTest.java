package com.justorder.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.model.Dish;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.AlergenRepository;

@WebMvcTest(DishController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DishControllerTest {

    @Autowired private MockMvc mockMvc; @org.springframework.test.context.bean.override.mockito.MockitoBean private com.justorder.backend.security.JwtUtil jwtUtil;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @MockitoBean private DishRepository dishRepository;
    @MockitoBean private RestaurantRepository restaurantRepository;
    @MockitoBean private AlergenRepository alergenRepository;

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
}