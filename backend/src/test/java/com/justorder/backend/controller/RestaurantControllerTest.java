package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.security.JwtUtil;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.everyItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class RestaurantControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void testGetMenu() throws Exception {
        mockMvc.perform(get("/api/restaurants/1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name=='Four Cheese Pizza' && @.description=='Stone-baked pizza with four cheeses' && @.price==23.0 && @.restaurantId==1 && @.allergenNames==[\"Gluten\",\"Lactose\"])]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.name=='Grilled Salmon' && @.description=='Grilled salmon fillet with herbs' && @.price==25.0 && @.restaurantId==1 && @.allergenNames==[])]", hasSize(1)));
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
    void testSearchAllRestaurants() throws Exception {
        mockMvc.perform(get("/api/restaurants/search"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void testSearchByCuisine() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("cuisine", "italian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cuisineCategoryNames[0]").value("Italian"));
    }

    @Test
    void testSearchByCuisineCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("cuisine", "ITALIAN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cuisineCategoryNames[0]").value("Italian"));
    }

    @Test
    void testSearchByMinRating() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("minRating", "4.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].averageRating", everyItem(greaterThanOrEqualTo(4.0))));
    }

    @Test
    void testSearchByMaxPrice() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("maxPrice", "8.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testSearchByMinPrice() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("minPrice", "13.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void testSearchByCuisineAndMinRating() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("cuisine", "japanese")
                .param("minRating", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Sushi Tokyo"));
    }

    @Test
    void testSearchReturnsEmptyListWhenNoMatch() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("cuisine", "indian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetRestaurantProfileWithValidToken() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        mockMvc.perform(get("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("lamarina@justorder.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testUpdateRestaurantProfileWithValidToken() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        String updateBody = """
        {
            "name": "La Marina Renewed",
            "description": "Updated seafood and grilled specialties",
            "phone": "600999888",
            "mondayWorkingHours": "09:00-18:00",
            "tuesdayWorkingHours": "09:30-18:30",
            "wednesdayWorkingHours": "10:00-19:00",
            "thursdayWorkingHours": "10:30-19:30",
            "fridayWorkingHours": "11:00-20:00",
            "saturdayWorkingHours": "11:30-20:30",
            "sundayWorkingHours": "12:00-21:00"
        }
        """;

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("La Marina Renewed"))
                .andExpect(jsonPath("$.description").value("Updated seafood and grilled specialties"))
                .andExpect(jsonPath("$.phone").value("600999888"))
                .andExpect(jsonPath("$.mondayWorkingHours").value("09:00-18:00"))
                .andExpect(jsonPath("$.tuesdayWorkingHours").value("09:30-18:30"))
                .andExpect(jsonPath("$.wednesdayWorkingHours").value("10:00-19:00"))
                .andExpect(jsonPath("$.thursdayWorkingHours").value("10:30-19:30"))
                .andExpect(jsonPath("$.fridayWorkingHours").value("11:00-20:00"))
                .andExpect(jsonPath("$.saturdayWorkingHours").value("11:30-20:30"))
                .andExpect(jsonPath("$.sundayWorkingHours").value("12:00-21:00"))
                .andExpect(jsonPath("$.password").doesNotExist());         
    }

    @Test
    void testUpdateRestaurantProfileCuisineCategoriesWithValidToken() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        String updateBody = """
        {
            "cuisineCategoryNames": ["Italian", "Japanese"]
        }
        """;

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuisineCategoryNames", hasSize(2)));
    }

    @Test
    void testUpdateRestaurantProfileLocalizationsWithValidToken() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        String updateBody = """
        {
            "localizations": [
                {
                    "city": "Bilbao",
                    "province": "Bizkaia",
                    "country": "Spain",
                    "postalCode": "48002",
                    "number": "99",
                    "longitude": -2.934,
                    "latitude": 43.262
                }
            ]
        }
        """;

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.localizations", hasSize(1)))
                .andExpect(jsonPath("$.localizations[0].city").value("Bilbao"))
                .andExpect(jsonPath("$.localizations[0].province").value("Bizkaia"))
                .andExpect(jsonPath("$.localizations[0].country").value("Spain"))
                .andExpect(jsonPath("$.localizations[0].postalCode").value("48002"))
                .andExpect(jsonPath("$.localizations[0].number").value("99"));
    }

    @Test
    void testGetRestaurantDashboardWithValidToken() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        mockMvc.perform(get("/api/restaurants/dashboard")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").isNumber())
                .andExpect(jsonPath("$.totalOrders").isNumber())
                .andExpect(jsonPath("$.activeOrders").isNumber())
                .andExpect(jsonPath("$.cancelledOrders").isNumber())
                .andExpect(jsonPath("$.deliveredOrders").isNumber())
                .andExpect(jsonPath("$.totalRevenue").isNumber())
                .andExpect(jsonPath("$.totalRefunded").isNumber())
                .andExpect(jsonPath("$.recentOrders").isArray());
    }

    @Test
    void testGetRestaurantProfileWithoutAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/restaurants/profile"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRestaurantProfileWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/restaurants/profile")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetRestaurantDashboardWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/restaurants/dashboard")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetRestaurantProfileWithMalformedAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/restaurants/profile")
                .header("Authorization", "invalid-token-without-bearer"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateRestaurantProfileWithoutAuthorizationHeader() throws Exception {
        String updateBody = """
        {
            "name": "No Token"
        }
        """;

        mockMvc.perform(put("/api/restaurants/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRestaurantProfileWithInvalidWorkingHoursFormat() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        String updateBody = """
        {
            "mondayWorkingHours": "9:00-18:00"
        }
        """;

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRestaurantProfileWithUnknownCuisineCategory() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        String updateBody = """
        {
            "cuisineCategoryNames": ["UnknownCategory"]
        }
        """;

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRestaurantProfileWithEmptyCuisineCategoryList() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        String updateBody = """
        {
            "cuisineCategoryNames": []
        }
        """;

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRestaurantProfileWithEmptyLocalizationsList() throws Exception {
        String token = createRestaurantSessionAndGetToken();

        String updateBody = """
        {
            "localizations": []
        }
        """;

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isBadRequest());
    }

    private String createRestaurantSessionAndGetToken() throws Exception {
        String loginBody = """
                {
                    "type": "restaurant",
                    "email": "lamarina@justorder.com",
                    "password": "restaurant123"
                }
                """;

        MvcResult result = mockMvc.perform(post("/sessions/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }
}