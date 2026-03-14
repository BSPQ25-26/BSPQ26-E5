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
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void testRegisterRestaurant() throws Exception {
        String requestBody = """
        {
            "name": "Pizza Palace",
            "description": "Best pizza and pasta in Bilbao",
            "phone": "600123456",
            "email": "pizzapalace@example.com",
            "password": "securePaasdasdasssword123",
            "mondayWorkingHours": "10:00-22:00",
            "tuesdayWorkingHours": "10:00-22:00",
            "wednesdayWorkingHours": "10:00-22:00",
            "thursdayWorkingHours": "10:00-22:00",
            "fridayWorkingHours": "10:00-23:30",
            "saturdayWorkingHours": "12:00-23:30",
            "sundayWorkingHours": "12:00-21:00",
            "dishes": [],
            "cuisineCategoryNames": ["Italian"],
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
            ]
        }
        """;
        
        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterVoidRestaurant() throws Exception {
        String requestBody = """
        {
        }
        """;
        
        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterWrongHoursRestaurant() throws Exception {
        String requestBody = """
        {
            "name": "Pizza Palace",
            "description": "Best pizza and pasta in Bilbao",
            "phone": "600123456",
            "email": "pizzapgho,myhtalace@example.com",
            "password": "securePaasdasmujo7iudasssword123",
            "mondayWorkingHours": "10:0-22:00",
            "tuesdayWorkingHours": "10:00-22:00",
            "wednesdayWorkingHours": "10:0022:00",
            "thursdayWorkingHours": "10:00-22:00",
            "fridayWorkingHours": "10:00-23:30",
            "saturdayWorkingHours": "12:00-23:0",
            "sundayWorkingHours": "12:0021:00",
            "dishes": [],
            "cuisineCategoryNames": ["Italian"],
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
            ]
        }
        """;
        
        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterGhostRestaurant() throws Exception {
        String requestBody = """
        {
            "name": "Pizza Palace",
            "description": "Best pizza and pasta in Bilbao",
            "phone": "600123456",
            "email": "pizzap-`po8.,ialace@example.com",
            "password": "securePaasdasmujo7iudasssword123",
            "mondayWorkingHours": "10:00-22:00",
            "tuesdayWorkingHours": "10:00-22:00",
            "wednesdayWorkingHours": "10:00-22:00",
            "thursdayWorkingHours": "10:00-22:00",
            "fridayWorkingHours": "10:00-23:30",
            "saturdayWorkingHours": "12:00-23:30",
            "sundayWorkingHours": "12:00-21:00",
            "dishes": [],
            "cuisineCategoryNames": ["Italian"],
            "localizations": [
            ]
        }
        """;
        
        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteAllRestaurants() throws Exception {
        mockMvc.perform(delete("/api/restaurants"))
               .andExpect(status().isOk());
    }
    
}
