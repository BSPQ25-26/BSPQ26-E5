package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    // RUTA PARA EL ADMIN: Devuelve todos los restaurantes de la base de datos
    @GetMapping("/all")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return ResponseEntity.ok(restaurants);
    }
    
    @PostMapping("/create")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody RestaurantDTO request) {
        // 1. Creamos un restaurante vacío
        Restaurant newRestaurant = new Restaurant();
        
        // 2. Lo rellenamos con los datos que llegan desde el formulario de React
        newRestaurant.setName(request.getName());
        newRestaurant.setDescription(request.getDescription());
        newRestaurant.setEmail(request.getEmail());
        newRestaurant.setPhone(request.getPhone());
        newRestaurant.setPassword(request.getPassword()); 
        
        // 3. Lo guardamos en la base de datos
        Restaurant savedRestaurant = restaurantRepository.save(newRestaurant);
        
        // 4. Devolvemos un OK a React
        return ResponseEntity.ok(savedRestaurant);
    }
}