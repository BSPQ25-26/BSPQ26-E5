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
import com.justorder.backend.dto.CustomerDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.repository.CustomerRepository;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTest {

    @Autowired private MockMvc mockMvc; @org.springframework.test.context.bean.override.mockito.MockitoBean private com.justorder.backend.security.JwtUtil jwtUtil;
    private ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean private CustomerRepository repository;

    @Test
    public void testGetAll() throws Exception {
        Customer c = new Customer();
        c.setId(1L);
        c.setName("Juan Perez");
        
        when(repository.findAll()).thenReturn(Arrays.asList(c));

        mockMvc.perform(get("/api/customers/all"))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Juan Perez")));
    }

    @Test
    public void testCreate() throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setName("Ana Gomez");

        Customer saved = new Customer();
        saved.setId(2L);
        saved.setName("Ana Gomez");

        when(repository.save(any(Customer.class))).thenReturn(saved);

        mockMvc.perform(post("/api/customers/create")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Ana Gomez")));
    }

    @Test
    public void testUpdate() throws Exception {
        CustomerDTO request = new CustomerDTO();
        request.setName("Ana Modificada");

        Customer existing = new Customer();
        existing.setId(2L);
        existing.setName("Ana Gomez");

        Customer updated = new Customer();
        updated.setId(2L);
        updated.setName("Ana Modificada");

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenReturn(updated);

        mockMvc.perform(put("/api/customers/update/2")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Ana Modificada")));
    }

    @Test
    public void testDelete() throws Exception {
        when(repository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/customers/delete/1"))
               .andExpect(status().isOk());
    }
}