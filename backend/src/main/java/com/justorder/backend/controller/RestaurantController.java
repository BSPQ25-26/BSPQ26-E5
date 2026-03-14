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
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.service.MenuService;

import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.service.RegisterService;
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private MenuService menuService;
    
    private final RegisterService registerService;
    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RegisterService registerService, RestaurantRepository restaurantRepository) {
        this.registerService = registerService;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateRestaurant(@RequestBody RestaurantDTO request) {
        //TODO: implement
        try {
            this.registerService.registerRestaurant(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/menu")
    public ResponseEntity<HttpStatus> createOrUpdateMenu(@RequestBody List<DishDTO> request) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable String restaurantId) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // GET /api/restaurants/{restaurantId}/menu returns the menu of the restaurant with the given id
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<DishDTO>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }

    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllRestaurants() {
        restaurantRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
