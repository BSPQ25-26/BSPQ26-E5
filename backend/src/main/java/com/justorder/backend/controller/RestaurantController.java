package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    // Nos conectamos a la tabla de restaurantes que ya tienes creada
    @Autowired
    private RestaurantRepository restaurantRepository;

    // RUTA PARA EL ADMIN: Devuelve todos los restaurantes de la base de datos
    @GetMapping("/all")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return ResponseEntity.ok(restaurants);
    }
}