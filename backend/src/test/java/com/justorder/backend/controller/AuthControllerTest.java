package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.model.Admin;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AdminRepository adminRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private RiderRepository riderRepository;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void testLoginAdminSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@test.com");
        request.setPassword("password");

        Admin admin = new Admin();
        admin.setEmail("admin@test.com");
        admin.setPassword("encodedPassword");

        when(adminRepository.findByEmail(anyString())).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("admin@test.com", "ROLE_ADMIN")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginAdminFailure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@test.com");
        request.setPassword("wrongpassword");

        Admin admin = new Admin();
        admin.setEmail("admin@test.com");
        admin.setPassword("encodedPassword");

        when(adminRepository.findByEmail(anyString())).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}