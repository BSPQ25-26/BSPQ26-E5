package com.justorder.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.justorder.backend.security.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testCreateCustomerSessionUsingSeededData() throws Exception {
        String requestBody = """
                {
                    "type": "customer",
                    "email": "customer@test.com",
                    "password": "customer123"
                }
                """;

        mockMvc.perform(post("/sessions/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.customer.email").value("customer@test.com"));
    }

    @Test
    void testCreateRiderSessionUsingSeededData() throws Exception {
        String requestBody = """
                {
                    "type": "rider",
                    "email": "carlos.rider@test.com",
                    "password": "rider123"
                }
                """;

        mockMvc.perform(post("/sessions/riders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.rider.email").value("carlos.rider@test.com"));
    }

    @Test
    void testCreateRestaurantSessionUsingSeededData() throws Exception {
        String requestBody = """
                {
                    "type": "restaurant",
                    "email": "lamarina@justorder.com",
                    "password": "restaurant123"
                }
                """;

        mockMvc.perform(post("/sessions/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.restaurant.email").value("lamarina@justorder.com"));
    }

    @Test
    void testCreateSessionWithWrongTypeReturnsBadRequest() throws Exception {
        String requestBody = """
                {
                    "type": "admin",
                    "email": "customer@test.com",
                    "password": "anything"
                }
                """;

        mockMvc.perform(post("/sessions/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteCustomerSession() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/sessions/users")
                .header("Authorization", "Bearer random-token"))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteRiderSession() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/sessions/riders")
                .header("Authorization", "Bearer random-token"))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteRestaurantSession() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/sessions/restaurants")
                .header("Authorization", "Bearer random-token"))
            .andExpect(status().isOk());
    }
}
