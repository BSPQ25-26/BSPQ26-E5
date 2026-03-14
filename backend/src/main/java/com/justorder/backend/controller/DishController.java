package com.justorder.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.exception.DishConflictException;
import com.justorder.backend.exception.InvalidDishDataException;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.service.MenuService;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private MenuService menuService;

    // POST /api/dishes/{restaurantId} creates a new dish for the restaurant with the given id
    @PostMapping("/{restaurantId}")
    public ResponseEntity<DishDTO> createDish(@PathVariable Long restaurantId,
                                               @RequestBody DishDTO dishDTO) {
        try {
            if (dishDTO.getRestaurantId() != null && !dishDTO.getRestaurantId().equals(restaurantId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
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

    // PUT /api/dishes/{dishId} updates an existing dish
    @PutMapping("/{dishId}")
    public ResponseEntity<DishDTO> updateDish(@PathVariable Long dishId,
                                               @RequestBody DishDTO dishDTO) {
        try {
            DishDTO updated = menuService.updateDish(dishId, dishDTO);
            return ResponseEntity.ok(updated);
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

    // DELETE /api/dishes/{dishId} deletes the dish with the given id
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
