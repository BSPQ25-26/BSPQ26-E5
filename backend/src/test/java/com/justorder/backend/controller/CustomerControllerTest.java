package com.justorder.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import com.justorder.backend.security.JwtUtil;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String getAuthToken() throws Exception {
        // Login to get the token with the existing seeded customer
        String loginBody = """
            {
                "type": "customer",
                "email": "customer@test.com",
                "password": "customer123"
            }
            """;
        MvcResult result = mockMvc.perform(post("/sessions/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andExpect(status().isOk())
            .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("token").asText();
    }

    @Test
    void testRegisterCustomer() throws Exception {
        String requestBody = """
            {
                "name": "John Doe Test",
                "email": "johndoTeste@example.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345678ATest",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
    }

    @Test
    void testRegisterHomelessCustomer() throws Exception {
        String requestBody = """
            {
                "name": "Homeless John Doe",
                "email": "johndoe@example.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345678A",
                "localizations": [
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterFalseEmailCustomer() throws Exception {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "johndoeexample.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345678A",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterShortPasswordCustomer() throws Exception {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "johndoeexample.com",
                "phone": "600123456",
                "password": "suword123",
                "age": 30,
                "dni": "12345678A",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterVoidCustomer() throws Exception {

        String requestBody = """
            {
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterNoAllergenCustomer() throws Exception {
        String requestBody = """
            {
                "name": "John Doe Non Allergen",
                "email": "olik@example.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345asd678A",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": [],
                "preferenceNames": ["Italian", "Japanese"]
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
    }
    
    @Test
    void testRegisterNoPreferenceCustomer() throws Exception {
        String requestBody = """
            {
                "name": "John Doe No Preference",
                "email": "johnadgdoe@example.com",
                "phone": "600123456",
                "password": "supersecurepassword123",
                "age": 30,
                "dni": "12345kiik678A",
                "localizations": [
                    {
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                ],
                "allergenNames": ["Gluten", "Lactose"],
                "preferenceNames": []
            }
            """;
        mockMvc.perform(post("/api/customers/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteAllCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetCustomerDashboard() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/customers/dashboard")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerName").value("Test Customer"))
            .andExpect(jsonPath("$.totalOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.activeOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.cancelledOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.deliveredOrders").value(greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.totalSpent").isNumber())
            .andExpect(jsonPath("$.totalRefunded").isNumber())
            .andExpect(jsonPath("$.recentOrders").isArray());
    }

    @Test
    void testGetCustomerDashboardUnauthorized() throws Exception {
        mockMvc.perform(get("/api/customers/dashboard"))
            .andExpect(status().isBadRequest()); // O depende de SecurityFilterChain
    }
    
    @Test
    void testGetCustomerProfile() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/customers/profile")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test Customer"))
            .andExpect(jsonPath("$.email").value("customer@test.com"));
    }

    @Test
    void testGetCustomerOrders() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/customers/orders")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

}
