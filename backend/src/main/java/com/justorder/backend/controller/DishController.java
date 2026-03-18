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

/**
 * REST controller for managing Dish entities.
 * Provides endpoints for performing CRUD (Create, Read, Update, Delete) 
 * operations on dishes, including the management of their relationships 
 * with restaurants and allergens.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishRepository dishRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private AlergenRepository alergenRepository;

    /**
     * Retrieves a list of all available dishes in the database.
     * To prevent JSON serialization issues (infinite recursion), the 'dishes' 
     * list within each associated restaurant is explicitly set to null before returning.
     * * @return a ResponseEntity containing a list of {@link Dish} objects and an HTTP 200 OK status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        
        for (Dish dish : dishes) {
            if (dish.getRestaurant() != null) {
                dish.getRestaurant().setDishes(null);
            }
        }
        
        return ResponseEntity.ok(dishes);
    }

    /**
     * Creates a new dish and saves it to the database.
     * Maps the provided restaurant ID and allergen IDs from the request DTO 
     * to their respective database entities.
     * * @param request the data transfer object containing the details of the dish to be created.
     * @return a ResponseEntity containing the newly created {@link Dish} and an HTTP 200 OK status.
     */
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

        Dish savedDish = dishRepository.save(newDish);
        
        if (savedDish.getRestaurant() != null) {
            savedDish.getRestaurant().setDishes(null);
        }

        return ResponseEntity.ok(savedDish);
    }

    /**
     * Updates an existing dish identified by its ID.
     * Updates the basic details as well as its relationships with a restaurant and allergens.
     * * @param id the unique identifier of the dish to be updated.
     * @param request the data transfer object containing the updated details.
     * @return a ResponseEntity containing the updated {@link Dish} if found, 
     * or an HTTP 404 Not Found status if the dish does not exist.
     */
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

            Dish updatedDish = dishRepository.save(existingDish);
            
            if (updatedDish.getRestaurant() != null) {
                updatedDish.getRestaurant().setDishes(null);
            }

            return ResponseEntity.ok(updatedDish);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific dish by its ID.
     * * @param id the unique identifier of the dish to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if the deletion was successful, 
     * or an HTTP 404 Not Found status if the dish does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDish(@PathVariable Long id) {
        if (dishRepository.existsById(id)) {
            dishRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}