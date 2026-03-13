package com.justorder.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.justorder.backend.security.JwtAuthenticationFilter;
import com.justorder.backend.security.JwtUtil;
import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.service.RegisterService;

@WebMvcTest(RiderController.class)
class RiderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private RegisterService registerService;

    @Test
    void testCreateRider() throws Exception {
        String requestBody = """
                {
                    "name": "Rider Name",
                    "dni": "12345678A",
                    "phoneNumber": "600123456",
                    "email": "rider@example.com",
                    "password": "securePass123"
                }
                """;

        mockMvc.perform(post("/api/riders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
               .andExpect(status().isOk());
    }
}