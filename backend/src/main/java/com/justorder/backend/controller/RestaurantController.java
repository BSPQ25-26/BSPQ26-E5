package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Dish;
import com.justorder.backend.repository.RestaurantRepository;

/**
 * REST controller for managing Restaurant entities.
 * Provides endpoints for performing CRUD (Create, Read, Update, Delete) 
 * operations on restaurants and carefully handles bidirectional 
 * relationships with dishes to optimize JSON responses.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Retrieves a list of all available restaurants in the database.
     * To prevent infinite recursion during JSON serialization, the back-reference 
     * (the 'restaurant' field) inside each associated dish is explicitly set to null 
     * before returning the response.
     * * @return a ResponseEntity containing a list of {@link Restaurant} objects and an HTTP 200 OK status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        
        // Prevents infinite recursion during JSON serialization
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getDishes() != null) {
                for (Dish dish : restaurant.getDishes()) {
                    // Hides the restaurant back-reference within the dish solely for the JSON payload
                    dish.setRestaurant(null); 
                }
            }
        }
        
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Creates a newly registered restaurant and saves it to the database.
     * * @param request the data transfer object containing the details of the restaurant to be created.
     * @return a ResponseEntity containing the newly created {@link Restaurant} and an HTTP 200 OK status.
     */
    @PostMapping("/create")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody RestaurantDTO request) {
        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setName(request.getName());
        newRestaurant.setDescription(request.getDescription());
        newRestaurant.setEmail(request.getEmail());
        newRestaurant.setPhone(request.getPhone());
        newRestaurant.setPassword(request.getPassword()); 
        
        Restaurant savedRestaurant = restaurantRepository.save(newRestaurant);
        
        // Prevents infinite recursion in the returned JSON
        if (savedRestaurant.getDishes() != null) {
            for (Dish dish : savedRestaurant.getDishes()) {
                dish.setRestaurant(null);
            }
        }
        
        return ResponseEntity.ok(savedRestaurant);
    }

    /**
     * Deletes a specific restaurant by its ID.
     * * @param id the unique identifier of the restaurant to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if the deletion was successful, 
     * or an HTTP 404 Not Found status if the restaurant does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates an existing restaurant identified by its ID.
     * The password field is only updated if a new, non-empty value is provided.
     * * @param id the unique identifier of the restaurant to be updated.
     * @param request the data transfer object containing the updated details.
     * @return a ResponseEntity containing the updated {@link Restaurant} if found, 
     * or an HTTP 404 Not Found status if the restaurant does not exist.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO request) {
        return restaurantRepository.findById(id)
            .map(existingRestaurant -> {
                existingRestaurant.setName(request.getName());
                existingRestaurant.setDescription(request.getDescription());
                existingRestaurant.setEmail(request.getEmail());
                existingRestaurant.setPhone(request.getPhone());
                
                if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                    existingRestaurant.setPassword(request.getPassword());
                }
                
                Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
                
                // Prevents infinite recursion in the returned JSON
                if (updatedRestaurant.getDishes() != null) {
                    for (Dish dish : updatedRestaurant.getDishes()) {
                        dish.setRestaurant(null);
                    }
                }
                
                return ResponseEntity.ok(updatedRestaurant);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}