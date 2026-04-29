package com.justorder.backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Dishes")
public class DishController {

    private final MenuService menuService;

    /**
     * Constructor injection (Best practice compared to @Autowired field injection).
     */
    public DishController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Retrieves a list of all available dishes in the database.
     * @return a ResponseEntity containing a list of {@link DishDTO} objects.
     */
    @GetMapping
    @Operation(summary = "Get all dishes")
    public ResponseEntity<List<DishDTO>> getAllDishes() {
        return ResponseEntity.ok(menuService.getAllDishes());
    }

    /**
     * Creates a new dish for a specific restaurant.
     * @param restaurantId the unique identifier of the restaurant.
     * @param dishDTO the data transfer object containing dish details.
     * @return the created {@link DishDTO} and HTTP 201 Created status.
     */
    @Operation(summary = "Create a dish for a restaurant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dish created"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PostMapping("/{restaurantId}")
    public ResponseEntity<DishDTO> createDish(@PathVariable Long restaurantId, 
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dish payload") @RequestBody DishDTO dishDTO) {
        // Prevent inconsistent payloads where path ID and body ID mismatch
        if (dishDTO.getRestaurantId() != null && !restaurantId.equals(dishDTO.getRestaurantId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

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
    @Operation(summary = "Update a dish")
    @PutMapping("/{dishId}")
    public ResponseEntity<DishDTO> updateDish(@PathVariable Long dishId, 
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dish payload") @RequestBody DishDTO dishDTO) {
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

    /**
     * Deletes a specific dish by its ID.
     * @param dishId the unique identifier of the dish.
     * @return HTTP 204 No Content if successful.
     */
    @Operation(summary = "Delete a dish")
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