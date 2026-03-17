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
import com.justorder.backend.dto.CuisineCategoryDTO;
import com.justorder.backend.model.CuisineCategory;
import com.justorder.backend.repository.CuisineCategoryRepository;

@WebMvcTest(CuisineCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CuisineCategoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CuisineCategoryRepository repository;

    @Test
    public void testGetAll() throws Exception {
        CuisineCategory c = new CuisineCategory();
        c.setId(1L);
        c.setName("Italiana");
        
        when(repository.findAll()).thenReturn(Arrays.asList(c));

        mockMvc.perform(get("/api/cuisineCategories/all"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Italiana")));
    }

    @Test
    public void testCreate() throws Exception {
        CuisineCategoryDTO request = new CuisineCategoryDTO();
        request.setName("Japonesa");

        CuisineCategory saved = new CuisineCategory();
        saved.setId(2L);
        saved.setName("Japonesa");

        when(repository.save(any(CuisineCategory.class))).thenReturn(saved);

        mockMvc.perform(post("/api/cuisineCategories/create")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Japonesa")));
    }

    @Test
    public void testUpdate() throws Exception {
        CuisineCategoryDTO request = new CuisineCategoryDTO();
        request.setName("Mexicana");

        CuisineCategory existing = new CuisineCategory();
        existing.setId(2L);
        existing.setName("Japonesa");

        CuisineCategory updated = new CuisineCategory();
        updated.setId(2L);
        updated.setName("Mexicana");

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(CuisineCategory.class))).thenReturn(updated);

        mockMvc.perform(put("/api/cuisineCategories/update/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Mexicana")));
    }

    @Test
    public void testDelete() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/cuisineCategories/delete/1"))
               .andExpect(status().isOk());
    }
}