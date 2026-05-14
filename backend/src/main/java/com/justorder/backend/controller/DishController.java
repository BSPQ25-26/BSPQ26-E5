package com.justorder.backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
 * @brief Controller for managing dishes in restaurants.
 *
 * This controller provides endpoints to create, update, delete,
 * and retrieve dishes associated with restaurants, leveraging the 
 * MenuService for business logic and exception handling.
 */
@RestController
@RequestMapping("/api/dishes")
@Tag(name = "Dishes", description = "Endpoints for restaurant dish management, including creation, updates, and deletion.")
public class DishController {

    private final MenuService menuService;

    /**
     * @brief Constructor injection for DishController.
     */
    public DishController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * @brief Retrieves a list of all available dishes in the database.
     *
     * @return A ResponseEntity containing a list of DishDTO objects.
     */
    @GetMapping
    @Operation(summary = "Get all dishes")
    public ResponseEntity<List<DishDTO>> getAllDishes() {
        return ResponseEntity.ok(menuService.getAllDishes());
    }

    /**
     * @brief Creates a new dish for a given restaurant.
     *
     * @param restaurantId Identifier of the restaurant.
     * @param dishDTO Dish data transfer object.
     * @return The created dish or an error status.
     */
    @PostMapping("/{restaurantId}")
    @Operation(summary = "Create a dish for a restaurant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dish created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found"),
        @ApiResponse(responseCode = "409", description = "Dish conflict")
    })
    public ResponseEntity<DishDTO> createDish(
            @PathVariable Long restaurantId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dish payload"
            )
            @RequestBody DishDTO dishDTO) {

        try {
            if (dishDTO.getRestaurantId() != null &&
                !dishDTO.getRestaurantId().equals(restaurantId)) {
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

    /**
     * @brief Updates an existing dish.
     *
     * @param dishId Identifier of the dish to update.
     * @param dishDTO Updated dish data.
     * @return Updated dish or error status.
     */
    @PutMapping("/{dishId}")
    @Operation(summary = "Update a dish")
    public ResponseEntity<DishDTO> updateDish(
            @PathVariable Long dishId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dish payload"
            )
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

    /**
     * @brief Deletes a dish by its identifier.
     *
     * @param dishId Identifier of the dish to delete.
     * @return No content if deletion is successful or error status.
     */
    @DeleteMapping("/{dishId}")
    @Operation(summary = "Delete a dish")
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