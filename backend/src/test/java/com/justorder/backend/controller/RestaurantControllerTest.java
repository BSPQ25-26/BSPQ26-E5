package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private RestaurantRepository repository;
    
    @Autowired
    private OrderRepository orderRepository; 

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("test-token-" + System.nanoTime());
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/api/restaurants"))
               .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        Restaurant existing = new Restaurant();
        existing.setName("Burger King");
        existing.setEmail("bk@test.com");
        existing = repository.save(existing);

        RestaurantDTO request = new RestaurantDTO();
        request.setName("Burger King Nuevo");

        mockMvc.perform(put("/api/restaurants/" + existing.getId())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Burger King Nuevo")));
    }

    @Test
    public void testDelete() throws Exception {
        Restaurant existing = new Restaurant();
        existing.setName("To Delete");
        existing = repository.save(existing);

        mockMvc.perform(delete("/api/restaurants/" + existing.getId()))
               .andExpect(status().isOk());
    }

    @Test
    public void testGetMenu() throws Exception {
        Long restId = repository.findAll().get(0).getId();
        
        mockMvc.perform(get("/api/restaurants/" + restId + "/menu"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray());
    }

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
                .andExpect(status().isCreated());
    }

    @Test
    void testRegisterVoidRestaurant() throws Exception {
        String requestBody = "{}";

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
            "localizations": []
        }
        """;

        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteAllRestaurants() throws Exception {
        mockMvc.perform(delete("/api/restaurants/all"))
               .andExpect(status().isOk());
    }

    @Test
    void testSearchAllRestaurants() throws Exception {
        mockMvc.perform(get("/api/restaurants/search"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchByCuisine() throws Exception {
        mockMvc.perform(get("/api/restaurants/search").param("cuisine", "italian"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchByCuisineCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/restaurants/search").param("cuisine", "ITALIAN"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchByMinRating() throws Exception {
        mockMvc.perform(get("/api/restaurants/search").param("minRating", "4.0"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray())
                 .andExpect(jsonPath("$[*].averageRating", everyItem(greaterThanOrEqualTo(4.0))));
    }

    @Test
    void testSearchByMaxPrice() throws Exception {
        mockMvc.perform(get("/api/restaurants/search").param("maxPrice", "8.0"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchByMinPrice() throws Exception {
        mockMvc.perform(get("/api/restaurants/search").param("minPrice", "13.5"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchByCuisineAndMinRating() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                 .param("cuisine", "japanese")
                 .param("minRating", "4.5"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchReturnsEmptyListWhenNoMatch() throws Exception {
        mockMvc.perform(get("/api/restaurants/search").param("cuisine", "indian"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isArray())
                 .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testRejectOrderSuccess() throws Exception {
        // Garantizamos explícitamente que el estado Cancelled exista en este contexto
        if (orderStatusRepository.findByStatusIgnoreCase("Cancelled").isEmpty()) {
            orderStatusRepository.saveAndFlush(new OrderStatus("Cancelled"));
        }

        // Filtramos para obtener de forma segura una orden que sí contenga Platos y Restaurante
        Order targetOrder = orderRepository.findAll().stream()
                .filter(o -> o.getDishes() != null && !o.getDishes().isEmpty() && o.getDishes().get(0).getRestaurant() != null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No orders with dishes found for test. Ensure DataInitializer is working."));
        
        Long orderId = targetOrder.getId();
        Long restaurantId = targetOrder.getDishes().get(0).getRestaurant().getId();

        String requestBody = """
        {
            "reason": "Out of pizza dough"
        }
        """;

        mockMvc.perform(post("/api/restaurants/" + restaurantId + "/orders/" + orderId + "/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Cancelled"))
                .andExpect(jsonPath("$.rejectionReason").value("Out of pizza dough"));
    }

    @Test
    void testRejectOrderNotFound() throws Exception {
        String requestBody = """
        {
            "reason": "This order does not exist"
        }
        """;

        mockMvc.perform(post("/api/restaurants/1/orders/9999/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound()); 
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

    @Test
    void testGetRestaurantProfileWithTokenFromDeletedRestaurant() throws Exception {
        String email = "temp-restaurant-delete@justorder.com";
        String password = "temporaryRestaurantPass123";
        String token = createTempRestaurantSessionAndGetToken(email, password);

        Long restaurantId = repository.findByEmail(email).get().getId();
        repository.deleteById(restaurantId);
        
        repository.flush(); // Ensure deletion is flushed

        mockMvc.perform(get("/api/restaurants/profile")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
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

    private String createTempRestaurantSessionAndGetToken(String email, String password) throws Exception {
        String createBody = """
        {
            "name": "Temp Restaurant",
            "description": "Temporary restaurant for tests",
            "phone": "600123457",
            "email": "%s",
            "password": "%s",
            "mondayWorkingHours": "10:00-22:00",
            "tuesdayWorkingHours": "10:00-22:00",
            "wednesdayWorkingHours": "10:00-22:00",
            "thursdayWorkingHours": "10:00-22:00",
            "fridayWorkingHours": "10:00-22:00",
            "saturdayWorkingHours": "10:00-22:00",
            "sundayWorkingHours": "10:00-22:00",
            "dishes": [],
            "cuisineCategoryNames": ["Italian"],
            "localizations": [
                {
                    "city": "Bilbao",
                    "province": "Bizkaia",
                    "country": "Spain",
                    "postalCode": "48001",
                    "number": "10",
                    "longitude": -2.9253,
                    "latitude": 43.2630
                }
            ]
        }
        """.formatted(email, password);

        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated());

        String loginBody = """
        {
            "type": "restaurant",
            "email": "%s",
            "password": "%s"
        }
        """.formatted(email, password);

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