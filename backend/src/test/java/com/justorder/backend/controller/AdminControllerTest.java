package com.justorder.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired 
    private MockMvc mockMvc; @org.springframework.test.context.bean.override.mockito.MockitoBean private com.justorder.backend.security.JwtUtil jwtUtil;

    @Test
    public void testGetAdminDashboard() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
               .andExpect(status().isOk())
               // Comprobamos directamente el texto sin usar Hamcrest
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("¡Acceso concedido!")));
    }
}