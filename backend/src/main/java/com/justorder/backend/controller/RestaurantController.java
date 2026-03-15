package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Dish; // IMPORTANTE: Añadimos la importación de Dish
import com.justorder.backend.repository.RestaurantRepository;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        
        // CORTAFUEGOS: Rompemos el bucle infinito de los platos
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getDishes() != null) {
                for (Dish dish : restaurant.getDishes()) {
                    dish.setRestaurant(null); // Ocultamos el restaurante del plato solo para el envío JSON
                }
            }
        }
        
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping("/create")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody RestaurantDTO request) {
        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setName(request.getName());
        newRestaurant.setDescription(request.getDescription());
        newRestaurant.setEmail(request.getEmail());
        newRestaurant.setPhone(request.getPhone());
        newRestaurant.setPassword(request.getPassword()); 
        
        Restaurant savedRestaurant = restaurantRepository.save(newRestaurant);
        
        if (savedRestaurant.getDishes() != null) {
            for (Dish dish : savedRestaurant.getDishes()) {
                dish.setRestaurant(null);
            }
        }
        
        return ResponseEntity.ok(savedRestaurant);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

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