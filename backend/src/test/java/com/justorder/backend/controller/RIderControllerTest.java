package com.justorder.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.justorder.backend.security.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RiderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void testCreateRider() throws Exception {
        String requestBody = """
                {
                    "name": "Rider Name",
                    "dni": "12345678A",
                    "phoneNumber": "600123456",
                    "email": "rider@example.com",
                    "password": "securePasdasdasdasdass123",
                    "starterPoint":{
                        "city": "Bilbao",
                        "province": "Bizkaia",
                        "country": "Spain",
                        "postalCode": "48001",
                        "number": "5",
                        "longitude": -2.9253,
                        "latitude": 43.2630
                    }
                }
                """;
        mockMvc.perform(post("/api/riders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
               .andExpect(status().isOk());
    }

    @Test
    void testCreateVoidRider() throws Exception {
        String requestBody = """
                {
                }
                """;
        mockMvc.perform(post("/api/riders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
               .andExpect(status().isInternalServerError());
    }
    
    @Test
    void testNonStartRider() throws Exception {
        String requestBody = """
                {
                    "name": "Rider Name",
                    "dni": "12345678ATest",
                    "phoneNumber": "600123456",
                    "email": "ridasdser@example.com",
                    "password": "securePasdasdasdasdass123"
                }
                """;
        mockMvc.perform(post("/api/riders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
               .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteAllRiders() throws Exception {
        mockMvc.perform(delete("/api/riders"))
            .andExpect(status().isOk());
    }
}