package com.justorder.backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.exception.DishConflictException;
import com.justorder.backend.exception.InvalidDishDataException;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.service.MenuService;

/**
 * REST controller for managing Dish entities.
 * Provides endpoints for performing CRUD operations on dishes, 
 * leveraging the MenuService for business logic and exception handling.
 */
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private MenuService menuService;

    /**
     * Retrieves a list of all available dishes in the database.
     * @return a ResponseEntity containing a list of {@link DishDTO} objects.
     */
    @GetMapping
    public ResponseEntity<List<DishDTO>> getAllDishes() {
        return ResponseEntity.ok(menuService.getAllDishes());
    }

    /**
     * Creates a new dish for a specific restaurant.
     * @param restaurantId the unique identifier of the restaurant.
     * @param dishDTO the data transfer object containing dish details.
     * @return the created {@link DishDTO} and HTTP 201 Created status.
     */
    @PostMapping("/{restaurantId}")
    public ResponseEntity<DishDTO> createDish(@PathVariable Long restaurantId, @RequestBody DishDTO dishDTO) {
        try {
            DishDTO created = menuService.createDish(restaurantId, dishDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (InvalidDishDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DishConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing dish.
     * @param dishId the unique identifier of the dish to be updated.
     * @param dishDTO the updated details.
     * @return the updated {@link DishDTO}.
     */
    @PutMapping("/{dishId}")
    public ResponseEntity<DishDTO> updateDish(@PathVariable Long dishId, @RequestBody DishDTO dishDTO) {
        try {
            DishDTO updated = menuService.updateDish(dishId, dishDTO);
            return ResponseEntity.ok(updated);
        } catch (InvalidDishDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a specific dish by its ID.
     * @param dishId the unique identifier of the dish.
     * @return HTTP 204 No Content if successful.
     */
    @DeleteMapping("/{dishId}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long dishId) {
        try {
            menuService.deleteDish(dishId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}