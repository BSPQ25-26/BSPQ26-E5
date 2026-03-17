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
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RestaurantControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private RestaurantRepository repository;

    @Test
    public void testGetAll() throws Exception {
        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Pizzería Luigi");
        
        when(repository.findAll()).thenReturn(Arrays.asList(r));

        mockMvc.perform(get("/api/restaurants/all"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Pizzería Luigi")));
    }

    @Test
    public void testCreate() throws Exception {
        RestaurantDTO request = new RestaurantDTO();
        request.setName("Burger King");

        Restaurant saved = new Restaurant();
        saved.setId(2L);
        saved.setName("Burger King");

        when(repository.save(any(Restaurant.class))).thenReturn(saved);

        mockMvc.perform(post("/api/restaurants/create")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Burger King")));
    }

    @Test
    public void testUpdate() throws Exception {
        RestaurantDTO request = new RestaurantDTO();
        request.setName("Burger King Nuevo");

        Restaurant existing = new Restaurant();
        existing.setId(2L);
        existing.setName("Burger King");

        Restaurant updated = new Restaurant();
        updated.setId(2L);
        updated.setName("Burger King Nuevo");

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Restaurant.class))).thenReturn(updated);

        mockMvc.perform(put("/api/restaurants/update/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Burger King Nuevo")));
    }

    @Test
    public void testDelete() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/restaurants/delete/1"))
               .andExpect(status().isOk());
    }
}