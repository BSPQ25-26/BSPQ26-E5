package com.justorder.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.justorder.backend.dto.DishDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.dto.RejectionRequestDTO;
import com.justorder.backend.dto.RestaurantDTO;
import com.justorder.backend.dto.RestaurantDashboardDTO;
import com.justorder.backend.dto.RestaurantProfileUpdateDTO;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.service.MenuService;
import com.justorder.backend.service.OrderService;
import com.justorder.backend.service.RegisterService;
import com.justorder.backend.service.RestaurantService;
import com.justorder.backend.service.SessionService;

/**
 * REST controller for managing Restaurant entities.
 * Handles administrative CRUD operations, public searches, and menu management.
 */
@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurants")
public class RestaurantController {

    private static final Logger logger = LogManager.getLogger(RestaurantController.class);

    private final MenuService menuService;
    private final RegisterService registerService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final SessionService sessionService;

    /**
     * Constructor injection of all dependencies.
     */
    public RestaurantController(MenuService menuService,
                                RegisterService registerService,
                                RestaurantRepository restaurantRepository,
                                RestaurantService restaurantService,
                                OrderService orderService,
                                SessionService sessionService) {    
        this.menuService = menuService;
        this.registerService = registerService;
        this.restaurantRepository = restaurantRepository;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.sessionService = sessionService;
    }

    @GetMapping("/hello")
    @Operation(summary = "Health check for restaurant endpoints", description = "Simple hello endpoint to verify restaurant controller is reachable")
    public String hello() {
        logger.info("GET /api/restaurants/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }

    /**
     * Retrieves all restaurants converted to DTOs to avoid recursion.
     */
    @GetMapping
    @Operation(summary = "Get all restaurants")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<RestaurantDTO> results = restaurantRepository.findAll().stream()
                .map(Restaurant::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    /**
     * Registers a new restaurant using the RegisterService.
     */
    @Operation(summary = "Create or update a restaurant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurant created/updated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateRestaurant(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Restaurant payload") @RequestBody RestaurantDTO request) {
        try {
            logger.info("POST /api/restaurants/create - register restaurant request: {}", request != null ? request.getName() : "<null>");
            this.registerService.registerRestaurant(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            logger.error("Error registering restaurant", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing restaurant's details.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a restaurant")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO request) {
        return restaurantRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setDescription(request.getDescription());
            existing.setEmail(request.getEmail());
            existing.setPhone(request.getPhone());
            
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existing.setPassword(request.getPassword());
            }
            
            Restaurant updated = restaurantRepository.save(existing);
            return ResponseEntity.ok(updated.toDTO());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create or update menu for a restaurant")
    @PostMapping("/menu")
    public ResponseEntity<HttpStatus> createOrUpdateMenu(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Menu payload") @RequestBody List<DishDTO> request) {
       return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Operation(summary = "Get my restaurant profile", description = "Returns profile for the authenticated restaurant (Authorization header required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/profile")
    public ResponseEntity<RestaurantDTO> getMyProfile(@Parameter(description = "Authorization header: Bearer <token>") @RequestHeader("Authorization") String authorization) {
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

    @Operation(summary = "Update my restaurant profile")
    @PutMapping("/profile")
    public ResponseEntity<RestaurantDTO> updateMyProfile(
            @Parameter(description = "Authorization header: Bearer <token>") @RequestHeader("Authorization") String authorization,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Profile update payload") @RequestBody RestaurantProfileUpdateDTO request) {
        try {
            Long restaurantId = sessionService.getActiveRestaurantId(authorization);
            logger.info("PUT /api/restaurants/profile - update request for restaurant {}", restaurantId);
            RestaurantDTO updatedProfile = restaurantService.updateRestaurantProfile(restaurantId, request);
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

    @Operation(summary = "Get restaurant dashboard", description = "Returns dashboard stats for the authenticated restaurant")
    @GetMapping("/dashboard")
    public ResponseEntity<RestaurantDashboardDTO> getMyDashboard(@Parameter(description = "Authorization header: Bearer <token>") @RequestHeader("Authorization") String authorization) {
        try {
            Long restaurantId = sessionService.getActiveRestaurantId(authorization);
            logger.info("GET /api/restaurants/dashboard - dashboard request for restaurant {}", restaurantId);
            RestaurantDashboardDTO dashboard = restaurantService.getRestaurantDashboard(restaurantId);
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
     * Retrieves a specific restaurant by ID.
     */
    @Operation(summary = "Get restaurant by id")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@Parameter(description = "ID of the restaurant") @PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Deletes a specific restaurant by ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant by id")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Bulk deletion of all restaurants (Admin utility).
     */
    @Operation(summary = "Delete all restaurants")
    @DeleteMapping("/all")
    public ResponseEntity<HttpStatus> deleteAllRestaurants() {
        restaurantRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    /**
     * Searches for restaurants based on cuisine, rating, and price range.
     */
    @Operation(summary = "Search restaurants")
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
            @Parameter(description = "Cuisine type") @RequestParam(required = false) String cuisine,
            @Parameter(description = "Minimum rating") @RequestParam(required = false) Double minRating,
            @Parameter(description = "Minimum price") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) Double maxPrice) {

        List<RestaurantDTO> results = restaurantService.searchRestaurants(
            cuisine, minRating, minPrice, maxPrice
        );
        return ResponseEntity.ok(results);
    }

    /**
     * Retrieves the menu for a specific restaurant.
     */
    @Operation(summary = "Get menu for a restaurant")
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<DishDTO>> getMenu(@Parameter(description = "ID of the restaurant") @PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }
    
    /**
     * Rejects an order for a specific restaurant.
     */
    @Operation(summary = "Reject an order as restaurant")
    @PostMapping("/{restaurantId}/orders/{orderId}/reject")
    public ResponseEntity<OrderDTO> rejectOrder(
            @Parameter(description = "Restaurant id") @PathVariable("restaurantId") Long restaurantId, 
            @Parameter(description = "Order id") @PathVariable("orderId") Long orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Rejection reason") @RequestBody RejectionRequestDTO rejectionRequest) {
        
        OrderDTO updatedOrder = orderService.rejectOrder(restaurantId, orderId, rejectionRequest.getReason());
        return ResponseEntity.ok(updatedOrder);
    }
}