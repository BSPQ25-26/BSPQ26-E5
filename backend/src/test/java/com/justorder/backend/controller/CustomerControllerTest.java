package com.justorder.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import com.justorder.backend.security.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

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
}