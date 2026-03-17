package com.justorder.backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue; // Importación añadida

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.model.Admin;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.security.JwtUtil;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AdminRepository adminRepository;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private JwtUtil jwtUtil;

    @Test
    public void testLoginAdminSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@justorder.com");
        request.setPassword("1234");

        Admin admin = new Admin();
        admin.setEmail("admin@justorder.com");
        admin.setPassword("passwordEncriptada");

        when(adminRepository.findByEmail("admin@justorder.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("1234", "passwordEncriptada")).thenReturn(true);
        when(jwtUtil.generateToken("admin@justorder.com", "ROLE_ADMIN")).thenReturn("token-falso-123");

        mockMvc.perform(post("/api/auth/admin/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               // ¡Corregido! Verificamos el texto directamente
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("token-falso-123")));
    }

    @Test
    public void testLoginAdminFailure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@justorder.com");
        request.setPassword("claveMala");

        Admin admin = new Admin();
        admin.setEmail("admin@justorder.com");
        admin.setPassword("passwordEncriptada");

        when(adminRepository.findByEmail("admin@justorder.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("claveMala", "passwordEncriptada")).thenReturn(false);

        mockMvc.perform(post("/api/auth/admin/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isUnauthorized());
    }
}