package com.justorder.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justorder.backend.dto.*;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.security.JwtUtil;
import com.justorder.backend.service.*;
import com.justorder.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Magia pura: Solo cargamos la capa Web, nada de bases de datos ni contextos pesados.
@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RestaurantControllerTest {

    @Autowired 
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    // Mockeamos todas las dependencias del Controlador para tener control absoluto
    @MockitoBean private MenuService menuService;
    @MockitoBean private RegisterService registerService;
    @MockitoBean private RestaurantRepository restaurantRepository;
    @MockitoBean private RestaurantService restaurantService;
    @MockitoBean private OrderService orderService;
    @MockitoBean private SessionService sessionService;
    @MockitoBean private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("test-token-123");
    }

    @Test
    public void testGetAll() throws Exception {
        when(restaurantRepository.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants")).andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        Restaurant existing = new Restaurant(); 
        existing.setId(1L); 
        existing.setName("Old");
        
        Restaurant updated = new Restaurant(); 
        updated.setId(1L); 
        updated.setName("Burger King Nuevo");
        
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(restaurantRepository.save(any())).thenReturn(updated);

        RestaurantDTO request = new RestaurantDTO();
        request.setName("Burger King Nuevo");

        mockMvc.perform(put("/api/restaurants/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Burger King Nuevo")));
    }

    @Test
    public void testDelete() throws Exception {
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/restaurants/1")).andExpect(status().isOk());
    }

    @Test
    public void testGetMenu() throws Exception {
        when(menuService.getMenu(1L)).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants/1/menu")).andExpect(status().isOk());
    }

    @Test
    void testRegisterRestaurant() throws Exception {
        String requestBody = """
        { "name": "Pizza Palace", "description": "Best pizza and pasta in Bilbao", "phone": "600123456", "email": "pizzapalace@example.com", "password": "securePaasdasdasssword123", "mondayWorkingHours": "10:00-22:00", "tuesdayWorkingHours": "10:00-22:00", "wednesdayWorkingHours": "10:00-22:00", "thursdayWorkingHours": "10:00-22:00", "fridayWorkingHours": "10:00-23:30", "saturdayWorkingHours": "12:00-23:30", "sundayWorkingHours": "12:00-21:00", "dishes": [], "cuisineCategoryNames": ["Italian"], "localizations": [] }
        """;
        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegisterVoidRestaurant() throws Exception {
        doThrow(new RuntimeException("Validation Error")).when(registerService).registerRestaurant(any());
        
        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterWrongHoursRestaurant() throws Exception {
        doThrow(new RuntimeException("Bad Hours")).when(registerService).registerRestaurant(any());
        
        String requestBody = "{ \"mondayWorkingHours\": \"10:0-22:00\" }";
        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterGhostRestaurant() throws Exception {
        doThrow(new RuntimeException("No Location")).when(registerService).registerRestaurant(any());
        
        String requestBody = "{ \"localizations\": [] }";
        mockMvc.perform(post("/api/restaurants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteAllRestaurants() throws Exception {
        mockMvc.perform(delete("/api/restaurants/all")).andExpect(status().isOk());
    }

    @Test
    void testSearchAllRestaurants() throws Exception {
        when(restaurantService.searchRestaurants(any(), any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants/search")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchByCuisine() throws Exception {
        when(restaurantService.searchRestaurants(eq("italian"), any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants/search").param("cuisine", "italian")).andExpect(status().isOk());
    }

    @Test
    void testSearchByCuisineCaseInsensitive() throws Exception {
        when(restaurantService.searchRestaurants(eq("ITALIAN"), any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants/search").param("cuisine", "ITALIAN")).andExpect(status().isOk());
    }

    @Test
    void testSearchByMinRating() throws Exception {
        RestaurantDTO dto = new RestaurantDTO(); 
        dto.setAverageRating(4.5);
        when(restaurantService.searchRestaurants(any(), eq(4.0), any(), any())).thenReturn(List.of(dto));
        
        mockMvc.perform(get("/api/restaurants/search").param("minRating", "4.0"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$[*].averageRating", everyItem(greaterThanOrEqualTo(4.0))));
    }

    @Test
    void testSearchByMaxPrice() throws Exception {
        when(restaurantService.searchRestaurants(any(), any(), any(), eq(8.0))).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants/search").param("maxPrice", "8.0")).andExpect(status().isOk());
    }

    @Test
    void testSearchByMinPrice() throws Exception {
        when(restaurantService.searchRestaurants(any(), any(), eq(13.5), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants/search").param("minPrice", "13.5")).andExpect(status().isOk());
    }

    @Test
    void testSearchByCuisineAndMinRating() throws Exception {
        when(restaurantService.searchRestaurants(eq("japanese"), eq(4.5), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants/search").param("cuisine", "japanese").param("minRating", "4.5"))
                 .andExpect(status().isOk());
    }

    @Test
    void testSearchReturnsEmptyListWhenNoMatch() throws Exception {
        when(restaurantService.searchRestaurants(eq("indian"), any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/api/restaurants/search").param("cuisine", "indian"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testRejectOrderSuccess() throws Exception {
        // Al estar en WebMvcTest con Mock puro, es imposible que invoque a la Base de Datos.
        OrderDTO mockOrderDTO = new OrderDTO();
        mockOrderDTO.setId(1L);
        mockOrderDTO.setStatus("Cancelled");
        mockOrderDTO.setRejectionReason("Out of pizza dough");

        when(orderService.rejectOrder(eq(1L), eq(1L), anyString())).thenReturn(mockOrderDTO);

        mockMvc.perform(post("/api/restaurants/1/orders/1/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\": \"Out of pizza dough\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Cancelled"))
                .andExpect(jsonPath("$.rejectionReason").value("Out of pizza dough"));
    }

    @Test
    void testRejectOrderNotFound() throws Exception {
        when(orderService.rejectOrder(eq(1L), eq(9999L), anyString()))
            .thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(post("/api/restaurants/1/orders/9999/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\": \"This order does not exist\"}"))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void testGetRestaurantProfileWithValidToken() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        RestaurantDTO mockProfile = new RestaurantDTO();
        mockProfile.setId(1L);
        mockProfile.setEmail("lamarina@justorder.com");
        when(restaurantService.getRestaurantProfile(1L)).thenReturn(mockProfile);

        // Simulamos la cabecera directamente, sin usar SessionController
        mockMvc.perform(get("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("lamarina@justorder.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testUpdateRestaurantProfileWithValidToken() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        RestaurantDTO mockProfile = new RestaurantDTO();
        mockProfile.setName("La Marina Renewed");
        mockProfile.setMondayWorkingHours("09:00-18:00");
        
        when(restaurantService.updateRestaurantProfile(anyLong(), any())).thenReturn(mockProfile);

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("La Marina Renewed"))
                .andExpect(jsonPath("$.mondayWorkingHours").value("09:00-18:00"));     
    }

    @Test
    void testUpdateRestaurantProfileCuisineCategoriesWithValidToken() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        RestaurantDTO mockProfile = new RestaurantDTO();
        mockProfile.setCuisineCategoryNames(List.of("Italian", "Japanese"));
        when(restaurantService.updateRestaurantProfile(anyLong(), any())).thenReturn(mockProfile);

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuisineCategoryNames", hasSize(2)));
    }

    @Test
    void testUpdateRestaurantProfileLocalizationsWithValidToken() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        RestaurantDTO mockProfile = new RestaurantDTO();
        LocalizationDTO loc = new LocalizationDTO(); loc.setCity("Bilbao");
        mockProfile.setLocalizations(List.of(loc));
        when(restaurantService.updateRestaurantProfile(anyLong(), any())).thenReturn(mockProfile);

        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.localizations", hasSize(1)))
                .andExpect(jsonPath("$.localizations[0].city").value("Bilbao"));
    }

    @Test
    void testGetRestaurantDashboardWithValidToken() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        RestaurantDashboardDTO dash = new RestaurantDashboardDTO();
        dash.setRestaurantId(1L); 
        dash.setTotalOrders(0L); 
        when(restaurantService.getRestaurantDashboard(1L)).thenReturn(dash);

        mockMvc.perform(get("/api/restaurants/dashboard")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").isNumber())
                .andExpect(jsonPath("$.totalOrders").isNumber());
    }

    @Test
    void testGetRestaurantProfileWithoutAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/restaurants/profile"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRestaurantProfileWithInvalidToken() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenThrow(new SecurityException("Invalid token"));
        
        mockMvc.perform(get("/api/restaurants/profile")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetRestaurantDashboardWithInvalidToken() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenThrow(new SecurityException("Invalid token"));
        
        mockMvc.perform(get("/api/restaurants/dashboard")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetRestaurantProfileWithMalformedAuthorizationHeader() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenThrow(new SecurityException("Invalid token"));
        
        mockMvc.perform(get("/api/restaurants/profile")
                .header("Authorization", "invalid-token-without-bearer"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateRestaurantProfileWithoutAuthorizationHeader() throws Exception {
        mockMvc.perform(put("/api/restaurants/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRestaurantProfileWithInvalidWorkingHoursFormat() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        when(restaurantService.updateRestaurantProfile(anyLong(), any())).thenThrow(new IllegalArgumentException("Bad format"));
        
        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRestaurantProfileWithUnknownCuisineCategory() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        when(restaurantService.updateRestaurantProfile(anyLong(), any())).thenThrow(new IllegalArgumentException("Unknown Category"));
        
        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRestaurantProfileWithEmptyCuisineCategoryList() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        when(restaurantService.updateRestaurantProfile(anyLong(), any())).thenThrow(new IllegalArgumentException("Empty Category"));
        
        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRestaurantProfileWithEmptyLocalizationsList() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenReturn(1L);
        when(restaurantService.updateRestaurantProfile(anyLong(), any())).thenThrow(new IllegalArgumentException("Empty Localizations"));
        
        mockMvc.perform(put("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRestaurantProfileWithTokenFromDeletedRestaurant() throws Exception {
        when(sessionService.getActiveRestaurantId(anyString())).thenThrow(new SecurityException("Restaurant deleted"));
        
        mockMvc.perform(get("/api/restaurants/profile")
                .header("Authorization", "Bearer valid-token"))
            .andExpect(status().isUnauthorized());
    }
}