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
import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.RiderRepository;

@WebMvcTest(RiderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RiderControllerTest {

    @Autowired private MockMvc mockMvc; @org.springframework.test.context.bean.override.mockito.MockitoBean private com.justorder.backend.security.JwtUtil jwtUtil;
    private ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private RiderRepository repository;

    @Test
    public void testGetAll() throws Exception {
        Rider r = new Rider();
        r.setId(1L);
        r.setName("Carlos Moto");
        
        when(repository.findAll()).thenReturn(Arrays.asList(r));

        mockMvc.perform(get("/api/riders/all"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Carlos Moto")));
    }

    @Test
    public void testCreate() throws Exception {
        RiderDTO request = new RiderDTO();
        request.setName("Luis Bici");

        Rider saved = new Rider();
        saved.setId(2L);
        saved.setName("Luis Bici");

        when(repository.save(any(Rider.class))).thenReturn(saved);

        mockMvc.perform(post("/api/riders/create")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Luis Bici")));
    }

    @Test
    public void testUpdate() throws Exception {
        RiderDTO request = new RiderDTO();
        request.setName("Luis Modificado");

        Rider existing = new Rider();
        existing.setId(2L);
        existing.setName("Luis Bici");

        Rider updated = new Rider();
        updated.setId(2L);
        updated.setName("Luis Modificado");

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Rider.class))).thenReturn(updated);

        mockMvc.perform(put("/api/riders/update/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Luis Modificado")));
    }

    @Test
    public void testDelete() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/riders/delete/1"))
               .andExpect(status().isOk());
    }
}