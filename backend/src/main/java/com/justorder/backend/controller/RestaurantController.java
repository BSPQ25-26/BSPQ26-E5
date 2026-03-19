package com.justorder.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.service.MenuService;
import com.justorder.backend.service.RegisterService;
import com.justorder.backend.service.RestaurantService;

/**
 * REST controller for managing Restaurant entities.
 * Handles administrative CRUD operations, public searches, and menu management.
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final MenuService menuService;
    private final RegisterService registerService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;

    /**
     * Unified constructor for service and repository injection.
     */
    public RestaurantController(MenuService menuService,
                                RegisterService registerService,
                                RestaurantRepository restaurantRepository,
                                RestaurantService restaurantService) {
        this.menuService = menuService;
        this.registerService = registerService;
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }

    /**
     * Retrieves all restaurants converted to DTOs to avoid recursion.
     */
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<RestaurantDTO> results = restaurantRepository.findAll().stream()
                .map(Restaurant::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    /**
     * Registers a new restaurant using the RegisterService.
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createRestaurant(@RequestBody RestaurantDTO request) {
        try {
            this.registerService.registerRestaurant(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing restaurant's details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO request) {
        return restaurantRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setDescription(request.getDescription());
            existing.setEmail(request.getEmail());
            existing.setPhone(request.getPhone());
            
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existing.setPassword(request.getPassword());
            }
            
            Restaurant updated = restaurantRepository.save(existing);
            return ResponseEntity.ok(updated.toDTO());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific restaurant by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Searches for restaurants based on cuisine, rating, and price range.
     */
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        return ResponseEntity.ok(restaurantService.searchRestaurants(cuisine, minRating, minPrice, maxPrice));
    }

    /**
     * Retrieves the menu for a specific restaurant.
     */
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<DishDTO>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }

    /**
     * Bulk deletion of all restaurants (Admin utility).
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllRestaurants() {
        restaurantRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}