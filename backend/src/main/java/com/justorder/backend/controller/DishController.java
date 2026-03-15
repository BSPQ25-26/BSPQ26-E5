package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.model.Dish;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.AlergenRepository;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private AlergenRepository alergenRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(dishRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Dish> createDish(@RequestBody DishDTO request) {
        Dish newDish = new Dish();
        newDish.setName(request.getName());
        newDish.setDescription(request.getDescription());
        newDish.setPrice(request.getPrice());

        if (request.getRestaurantId() != null) {
            restaurantRepository.findById(request.getRestaurantId()).ifPresent(newDish::setRestaurant);
        }
        
        if (request.getAlergenIds() != null && !request.getAlergenIds().isEmpty()) {
            newDish.setAlergens(alergenRepository.findAllById(request.getAlergenIds()));
        }

        return ResponseEntity.ok(dishRepository.save(newDish));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody DishDTO request) {
        return dishRepository.findById(id).map(existingDish -> {
            existingDish.setName(request.getName());
            existingDish.setDescription(request.getDescription());
            existingDish.setPrice(request.getPrice());

            if (request.getRestaurantId() != null) {
                restaurantRepository.findById(request.getRestaurantId()).ifPresent(existingDish::setRestaurant);
            }
            
            if (request.getAlergenIds() != null) {
                existingDish.setAlergens(alergenRepository.findAllById(request.getAlergenIds()));
            }

            return ResponseEntity.ok(dishRepository.save(existingDish));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDish(@PathVariable Long id) {
        if (dishRepository.existsById(id)) {
            dishRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}