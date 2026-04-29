package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.*;
import com.justorder.backend.repository.*;
import com.justorder.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired private RestaurantRepository repository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderStatusRepository orderStatusRepository;
    @Autowired private DishRepository dishRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private RiderRepository riderRepository;

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
        // 1. Creamos TODOS los datos reales, vinculados a la perfección para que el OrderService real esté contento.
        Restaurant rest = new Restaurant();
        rest.setName("Pizza Palace Test");
        rest.setEmail("test" + System.nanoTime() + "@test.com");
        rest.setPassword("pass123");
        rest = repository.saveAndFlush(rest);

        Dish dish = new Dish("Pizza", "Good pizza", 10.0, rest);
        dish = dishRepository.saveAndFlush(dish);

        Customer customer = new Customer();
        customer.setName("Test Cust");
        customer.setEmail("cust" + System.nanoTime() + "@test.com");
        customer.setPhone("600123456");
        customer.setPassword("pass");
        customer.setAge(20);
        customer.setDni("12345678A");
        customer = customerRepository.saveAndFlush(customer);

        Localization loc = new Localization("Bilbao", "Bizkaia", "Spain", "48001", "1", 0.0, 0.0);
        Rider rider = new Rider("Test Rider", "12345678B", "+34 612345678", "rider" + System.nanoTime() + "@test.com", "pass", loc);
        rider = riderRepository.saveAndFlush(rider);

        OrderStatus pending = orderStatusRepository.findByStatus("Pending")
                .orElseGet(() -> orderStatusRepository.saveAndFlush(new OrderStatus("Pending")));
        
        OrderStatus cancelled = orderStatusRepository.findByStatus("Cancelled")
                .orElseGet(() -> orderStatusRepository.saveAndFlush(new OrderStatus("Cancelled")));

        Order order = new Order();
        order.setCustomer(customer);
        order.setDishes(List.of(dish));
        order.setStatus(pending);
        order.setRider(rider);
        order.setTotalPrice(10.0);
        order.setSecretCodeHash("hash123");
        order = orderRepository.saveAndFlush(order);

        String requestBody = "{\"reason\": \"Out of pizza dough\"}";

        // 2. Ejecutamos la petición. Como todo existe en BD, el servicio real devolverá 200 sin lanzar excepciones.
        mockMvc.perform(post("/api/restaurants/" + rest.getId() + "/orders/" + order.getId() + "/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Cancelled"))
                .andExpect(jsonPath("$.rejectionReason").value("Out of pizza dough"));
    }

    @Test
    void testRejectOrderNotFound() throws Exception {
        Restaurant rest = new Restaurant();
        rest.setName("Pizza Palace Not Found");
        rest.setEmail("notfound" + System.nanoTime() + "@test.com");
        rest.setPassword("pass123");
        rest = repository.saveAndFlush(rest);

        String requestBody = "{\"reason\": \"This order does not exist\"}";

        // Como usamos el servicio real, si pedimos una orden que no existe (999999), el mismo servicio lanza ResourceNotFoundException -> 404
        mockMvc.perform(post("/api/restaurants/" + rest.getId() + "/orders/999999/reject")
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