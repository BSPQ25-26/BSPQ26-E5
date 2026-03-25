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
import com.justorder.backend.dto.OrderStatusDTO;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.security.JwtUtil;

@WebMvcTest(OrderStatusController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderStatusControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean 
    private JwtUtil jwtUtil;

    @MockitoBean 
    private OrderStatusRepository repository;

    @Test
    public void testGetAll() throws Exception {
        OrderStatus status = new OrderStatus();
        status.setId(1L);
        status.setStatus("En Preparación");
        
        when(repository.findAll()).thenReturn(Arrays.asList(status));

        mockMvc.perform(get("/api/order-statuses/all"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("En Preparación")));
    }

    @Test
    public void testCreate() throws Exception {
        OrderStatusDTO request = new OrderStatusDTO();
        // CAMBIO: setName -> setStatus
        request.setStatus("En Camino"); 

        OrderStatus saved = new OrderStatus();
        saved.setId(2L);
        saved.setStatus("En Camino");

        when(repository.save(any(OrderStatus.class))).thenReturn(saved);

        mockMvc.perform(post("/api/order-statuses/create")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("En Camino")));
    }

    @Test
    public void testUpdate() throws Exception {
        OrderStatusDTO request = new OrderStatusDTO();
        // CAMBIO: setName -> setStatus
        request.setStatus("Entregado");

        OrderStatus existing = new OrderStatus();
        existing.setId(2L);
        existing.setStatus("En Camino");

        OrderStatus updated = new OrderStatus();
        updated.setId(2L);
        updated.setStatus("Entregado");

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(OrderStatus.class))).thenReturn(updated);

        mockMvc.perform(put("/api/order-statuses/update/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Entregado")));
    }

    @Test
    public void testDelete() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/order-statuses/delete/1"))
               .andExpect(status().isOk());
    }
}