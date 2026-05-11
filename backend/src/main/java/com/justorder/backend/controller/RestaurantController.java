package com.justorder.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.dto.RejectionRequestDTO;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.dto.RestaurantDashboardDTO;
import com.justorder.backend.dto.RestaurantProfileUpdateDTO;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.service.MenuService;
import com.justorder.backend.service.OrderService;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.service.RegisterService;
import com.justorder.backend.service.RestaurantService;
import com.justorder.backend.service.SessionService;

/**
 * @brief Controller for managing restaurant-related operations.
 *
 * This controller handles restaurant registration, profile management,
 * menus, orders, and dashboard analytics.
 */
@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurants")
public class RestaurantController {

    private static final Logger logger = LogManager.getLogger(RestaurantController.class);

    @Autowired
    private MenuService menuService;

    private final RegisterService registerService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;
    private final SessionService sessionService;
    private final OrderService orderService;

    public RestaurantController(RegisterService registerService,
                                RestaurantRepository restaurantRepository,
                                RestaurantService restaurantService,
                                OrderService orderService,
                                SessionService sessionService) {
        this.registerService = registerService;
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.sessionService = sessionService;
    }

    /**
     * @brief Health check endpoint for restaurant API.
     *
     * @return Simple greeting message confirming service availability.
     */
    @GetMapping("/hello")
    @Operation(summary = "Health check for restaurant endpoints", description = "Simple hello endpoint to verify restaurant controller is reachable")
    public String hello() {
        logger.info("GET /api/restaurants/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }

    /**
     * @brief Creates or updates a restaurant.
     *
     * @param request Restaurant data transfer object.
     * @return HTTP 200 if successful, HTTP 500 otherwise.
     */
    @PostMapping("/create")
    @Operation(summary = "Create or update a restaurant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant created/updated successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<HttpStatus> createOrUpdateRestaurant(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Restaurant payload"
            )
            @RequestBody RestaurantDTO request) {

        try {
            logger.info("POST /api/restaurants/create - register restaurant request: {}",
                    request != null ? request.getName() : "<null>");

            this.registerService.registerRestaurant(request);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error registering restaurant", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Creates or updates a restaurant menu.
     *
     * @param request List of dishes representing the menu.
     * @return HTTP 501 Not Implemented.
     */
    @PostMapping("/menu")
    @Operation(summary = "Create or update menu for a restaurant")
    public ResponseEntity<HttpStatus> createOrUpdateMenu(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Menu payload"
            )
            @RequestBody List<DishDTO> request) {

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * @brief Retrieves the authenticated restaurant profile.
     *
     * @param authorization JWT authorization header.
     * @return Restaurant profile data or error status.
     */
    @GetMapping("/profile")
    @Operation(summary = "Get my restaurant profile", description = "Returns profile for the authenticated restaurant (Authorization header required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<RestaurantDTO> getMyProfile(
            @Parameter(description = "Authorization header: Bearer <token>")
            @RequestHeader("Authorization") String authorization) {

        try {
            Long restaurantId = sessionService.getActiveRestaurantId(authorization);

            logger.info("GET /api/restaurants/profile - profile request for restaurant {}", restaurantId);

            RestaurantDTO profile = restaurantService.getRestaurantProfile(restaurantId);
            return ResponseEntity.ok(profile);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Updates the authenticated restaurant profile.
     *
     * @param authorization JWT authorization header.
     * @param request Profile update data.
     * @return Updated restaurant profile or error status.
     */
    @PutMapping("/profile")
    @Operation(summary = "Update my restaurant profile")
    public ResponseEntity<RestaurantDTO> updateMyProfile(
            @RequestHeader("Authorization") String authorization,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Profile update payload"
            )
            @RequestBody RestaurantProfileUpdateDTO request) {

        try {
            Long restaurantId = sessionService.getActiveRestaurantId(authorization);

            logger.info("PUT /api/restaurants/profile - update request for restaurant {}", restaurantId);

            RestaurantDTO updatedProfile =
                    restaurantService.updateRestaurantProfile(restaurantId, request);

            return ResponseEntity.ok(updatedProfile);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Retrieves restaurant dashboard statistics.
     *
     * @param authorization JWT authorization header.
     * @return Dashboard data or error status.
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get restaurant dashboard", description = "Returns dashboard stats for the authenticated restaurant")
    public ResponseEntity<RestaurantDashboardDTO> getMyDashboard(
            @RequestHeader("Authorization") String authorization) {

        try {
            Long restaurantId = sessionService.getActiveRestaurantId(authorization);

            logger.info("GET /api/restaurants/dashboard - dashboard request for restaurant {}", restaurantId);

            RestaurantDashboardDTO dashboard =
                    restaurantService.getRestaurantDashboard(restaurantId);

            return ResponseEntity.ok(dashboard);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Retrieves a restaurant by ID.
     *
     * @param restaurantId Restaurant identifier.
     * @return Not implemented placeholder.
     */
    @GetMapping("/{restaurantId}")
    @Operation(summary = "Get restaurant by id")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * @brief Retrieves the menu of a restaurant.
     *
     * @param restaurantId Restaurant identifier.
     * @return List of dishes.
     */
    @GetMapping("/{restaurantId}/menu")
    @Operation(summary = "Get menu for a restaurant")
    public ResponseEntity<List<DishDTO>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }

    /**
     * @brief Deletes all restaurants.
     *
     * WARNING: This operation removes all restaurant data.
     *
     * @return HTTP 200 on success.
     */
    @DeleteMapping()
    @Operation(summary = "Delete all restaurants")
    public ResponseEntity<HttpStatus> deleteAllRestaurants() {
        restaurantRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    /**
     * @brief Searches restaurants based on filters.
     *
     * @param cuisine Cuisine type filter.
     * @param minRating Minimum rating.
     * @param minPrice Minimum price.
     * @param maxPrice Maximum price.
     * @return List of matching restaurants.
     */
    @GetMapping("/search")
    @Operation(summary = "Search restaurants")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<RestaurantDTO> results =
                restaurantService.searchRestaurants(cuisine, minRating, minPrice, maxPrice);

        return ResponseEntity.ok(results);
    }

    /**
     * @brief Rejects an order for a restaurant.
     *
     * @param restaurantId Restaurant identifier.
     * @param orderId Order identifier.
     * @param rejectionRequest Rejection reason.
     * @return Updated order after rejection.
     */
    @PostMapping("/{restaurantId}/orders/{orderId}/reject")
    @Operation(summary = "Reject an order as restaurant")
    public ResponseEntity<OrderDTO> rejectOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Rejection reason"
            )
            @RequestBody RejectionRequestDTO rejectionRequest) {

        OrderDTO updatedOrder =
                orderService.rejectOrder(restaurantId, orderId, rejectionRequest.getReason());

        return ResponseEntity.ok(updatedOrder);
    }
}