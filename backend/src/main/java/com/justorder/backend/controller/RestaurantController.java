package com.justorder.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.service.MenuService;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.service.RegisterService;
import com.justorder.backend.service.RestaurantService;

/**
 * REST controller exposing the restaurant-related API endpoints.
 *
 * <p><b>Responsibility:</b> This controller handles only HTTP concerns:
 * parsing incoming request parameters and bodies, delegating all business
 * logic to the service layer, and wrapping results in the appropriate
 * {@link ResponseEntity} with the correct HTTP status code.</p>
 *
 * <p><b>What changed in this sprint (CA2):</b>
 * <ul>
 *   <li>Added {@link RestaurantService} as a dependency.</li>
 *   <li>Added the {@code GET /api/restaurants/search} endpoint for filtering.</li>
 * </ul>
 * </p>
 *
 * @see com.justorder.backend.service.RestaurantService
 * @see com.justorder.backend.service.RegisterService
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    /** Handles menu retrieval logic (CA1). */
    @Autowired
    private MenuService menuService;

    /**
     * Handles restaurant registration logic (IAM-2).
     * Shared with other controllers — not specific to restaurants only.
     */
    private final RegisterService registerService;

    /**
     * Direct repository access for the delete-all utility endpoint.
     *
     * <p><b>Tech debt:</b> This should eventually go through the service layer.
     * Direct controller-to-repository calls bypass business logic and make
     * the controller harder to test in isolation.</p>
     */
    private final RestaurantRepository restaurantRepository;

    /**
     * Handles restaurant search and filtering business logic (CA2).
     * Added in this sprint alongside the {@code /search} endpoint.
     */
    private final RestaurantService restaurantService;

    /**
     * Constructor injection of all dependencies.
     *
     * @param registerService      Service handling registration for all user types.
     * @param restaurantRepository JPA repository for direct DB access (see tech debt note).
     * @param restaurantService    Service handling restaurant search and filtering.
     */
    public RestaurantController(RegisterService registerService,
                                RestaurantRepository restaurantRepository,
                                RestaurantService restaurantService) {
        this.registerService = registerService;
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
    }

    /**
     * Health-check endpoint to verify the restaurant API is reachable.
     *
     * @return A plain text greeting string.
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }

    /**
     * Registers a new restaurant account (IAM-2).
     *
     * @param request The restaurant registration data from the request body.
     * @return {@code 200 OK} on success, {@code 500 Internal Server Error} on failure.
     */
    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateRestaurant(@RequestBody RestaurantDTO request) {
        try {
            this.registerService.registerRestaurant(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates or updates the menu for a restaurant (CA1).
     *
     * @param request List of dishes to set as the restaurant's menu.
     * @return {@code 501 Not Implemented} — pending CA1 implementation.
     */
    @PostMapping("/menu")
    public ResponseEntity<HttpStatus> createOrUpdateMenu(@RequestBody List<DishDTO> request) {
        // TODO: implement (CA1 — Restaurant Menu Management)
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Retrieves a single restaurant by its database ID.
     *
     * @param restaurantId The ID of the restaurant to retrieve.
     * @return {@code 501 Not Implemented} (pending implementation).
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable String restaurantId) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Retrieves the full menu (list of dishes) for a given restaurant (CA1).
     *
     * @param restaurantId The ID of the restaurant whose menu to retrieve.
     * @return {@code 200 OK} with the list of dishes for this restaurant.
     */
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<DishDTO>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }

    /**
     * Deletes all restaurants from the database.
     *
     * <p><b>Warning:</b> Development/testing utility — must be removed or
     * secured before any production deployment.</p>
     *
     * @return {@code 200 OK} after all restaurants are deleted.
     */
    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllRestaurants() {
        restaurantRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    /**
     * Searches and filters restaurants by cuisine, rating, and price range (CA2).
     *
     * <p>All query parameters are optional. Omitting a parameter means no filter
     * is applied for that field. Omitting all parameters returns every restaurant.</p>
     *
     * <p><b>Example requests:</b>
     * <pre>
     *   GET /api/restaurants/search
     *   GET /api/restaurants/search?cuisine=italian
     *   GET /api/restaurants/search?minRating=4
     *   GET /api/restaurants/search?minPrice=5&amp;maxPrice=20
     *   GET /api/restaurants/search?cuisine=japanese&amp;minRating=4&amp;maxPrice=30
     * </pre>
     *
     * @param cuisine   Cuisine category name (case-insensitive). {@code null} = no filter.
     * @param minRating Minimum average rating 0.0–5.0. {@code null} = no filter.
     * @param minPrice  Minimum dish price in euros. {@code null} = no filter.
     * @param maxPrice  Maximum dish price in euros. {@code null} = no filter.
     * @return {@code 200 OK} with matching {@link RestaurantDTO}s, empty list if none match.
     */
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<RestaurantDTO> results = restaurantService.searchRestaurants(
                cuisine, minRating, minPrice, maxPrice
        );
        return ResponseEntity.ok(results);
    }
}