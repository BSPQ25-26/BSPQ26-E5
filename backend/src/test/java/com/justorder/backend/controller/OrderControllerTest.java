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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Order;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.DishRepository;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderRepository orderRepository;
    @MockBean private CustomerRepository customerRepository;
    @MockBean private RiderRepository riderRepository;
    @MockBean private OrderStatusRepository orderStatusRepository;
    @MockBean private DishRepository dishRepository;

    @Test
    public void testGetAll() throws Exception {
        Order o = new Order();
        o.setId(1L);
        o.setSecretCode("CODE-123");
        
        when(orderRepository.findAll()).thenReturn(Arrays.asList(o));

        mockMvc.perform(get("/api/orders/all"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("CODE-123")));
    }

    @Test
    public void testCreate() throws Exception {
        OrderDTO request = new OrderDTO();
        request.setSecretCode("CODE-456");

        Order saved = new Order();
        saved.setId(2L);
        saved.setSecretCode("CODE-456");

        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        mockMvc.perform(post("/api/orders/create")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("CODE-456")));
    }

    @Test
    public void testUpdate() throws Exception {
        OrderDTO request = new OrderDTO();
        request.setSecretCode("CODE-789");

        Order existing = new Order();
        existing.setId(2L);
        existing.setSecretCode("CODE-456");

        Order updated = new Order();
        updated.setId(2L);
        updated.setSecretCode("CODE-789");

        when(orderRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenReturn(updated);

        mockMvc.perform(put("/api/orders/update/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("CODE-789")));
    }

    @Test
    public void testDelete() throws Exception {
        when(orderRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/orders/delete/1"))
               .andExpect(status().isOk());
    }
}