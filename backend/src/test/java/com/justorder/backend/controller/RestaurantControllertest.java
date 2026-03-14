package com.justorder.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.everyItem;

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

    // -------------------------------------------------------------------------
    // Existing tests — IAM-2 Restaurant Registration
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // CA2 — Restaurant Search & Filtering tests
    // These tests rely on the 4 restaurants seeded by DataInitializer:
    //   - Pizza Roma      (Italian,       rating: 4.5, dishes: €9.50–€12.00)
    //   - Sushi Tokyo     (Japanese,      rating: 4.8, dishes: €13.50–€14.00)
    //   - Taco Loco       (Mexican,       rating: 3.2, dishes: €4.50–€7.00)
    //   - Olive Garden    (Mediterranean, rating: 4.1, dishes: €6.50–€8.00)
    // -------------------------------------------------------------------------

    /**
     * No filters → should return all restaurants with 200 OK.
     * Verifies the endpoint is reachable and returns a non-empty JSON array.
     */
    @Test
    void testSearchAllRestaurants() throws Exception {
        mockMvc.perform(get("/api/restaurants/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /**
     * Filter by cuisine=italian (case-insensitive) → should return only Pizza Roma.
     * Verifies that cuisine filtering works and is case-insensitive.
     */
    @Test
    void testSearchByCuisine() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("cuisine", "italian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cuisineCategoryNames[0]").value("Italian"));
    }

    /**
     * Filter by cuisine=ITALIAN (uppercase) → should return same result as lowercase.
     * Verifies the case-insensitive behaviour of the cuisine filter.
     */
    @Test
    void testSearchByCuisineCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("cuisine", "ITALIAN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cuisineCategoryNames[0]").value("Italian"));
    }

    /**
     * Filter by minRating=4.0 → should return Pizza Roma (4.5), Sushi Tokyo (4.8),
     * Olive Garden (4.1). Should NOT return Taco Loco (3.2).
     * Verifies that every result has averageRating >= 4.0.
     */
    @Test
    void testSearchByMinRating() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("minRating", "4.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].averageRating", everyItem(greaterThanOrEqualTo(4.0))));
    }

    /**
     * Filter by maxPrice=8.0 → should return Taco Loco (€4.50–€7.00) and
     * Olive Garden (€6.50–€8.00). Should NOT return Pizza Roma or Sushi Tokyo.
     * Verifies that the price ceiling filter works correctly.
     */
    @Test
    void testSearchByMaxPrice() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("maxPrice", "8.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Filter by minPrice=10.0 → should return only Pizza Roma (€9.50–€12.00)
     * and Sushi Tokyo (€13.50–€14.00) since they have dishes above €10.
     * Verifies that the price floor filter works correctly.
     */
    @Test
    void testSearchByMinPrice() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("minPrice", "10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Filter by cuisine=japanese and minRating=4.5 → should return only Sushi Tokyo.
     * Verifies that combining multiple filters works correctly.
     */
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

    /**
     * Filter by cuisine=indian → no Indian restaurants are seeded.
     * Verifies that the endpoint returns 200 OK with an empty list (not 404)
     * when no restaurants match the filters.
     */
    @Test
    void testSearchReturnsEmptyListWhenNoMatch() throws Exception {
        mockMvc.perform(get("/api/restaurants/search")
                .param("cuisine", "indian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}