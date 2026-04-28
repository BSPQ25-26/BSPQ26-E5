package com.justorder.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public String hello() {
        logger.info("GET /api/restaurants/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }

    /**
     * Retrieves all restaurants converted to DTOs to avoid recursion.
     */
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<RestaurantDTO> results = restaurantRepository.findAll().stream()
                .map(Restaurant::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    /**
     * Registers a new restaurant using the RegisterService.
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createRestaurant(@RequestBody RestaurantDTO request) {
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

    @GetMapping("/profile")
    public ResponseEntity<RestaurantDTO> getMyProfile(@RequestHeader("Authorization") String authorization) {
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

    @PutMapping("/profile")
    public ResponseEntity<RestaurantDTO> updateMyProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody RestaurantProfileUpdateDTO request) {
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

    @GetMapping("/dashboard")
    public ResponseEntity<RestaurantDashboardDTO> getMyDashboard(@RequestHeader("Authorization") String authorization) {
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
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable String restaurantId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Creates or updates a menu for a restaurant.
     */
    @PostMapping("/menu")
    public ResponseEntity<HttpStatus> createOrUpdateMenu(@RequestBody List<DishDTO> request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Deletes a specific restaurant by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Searches for restaurants based on cuisine, rating, and price range.
     */
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        return ResponseEntity.ok(restaurantService.searchRestaurants(cuisine, minRating, minPrice, maxPrice));
    }

    /**
     * Retrieves the menu for a specific restaurant.
     */
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<DishDTO>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }

    /**
     * Bulk deletion of all restaurants (Admin utility).
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllRestaurants() {
        restaurantRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
    
    /**
     * Rejects an order for a specific restaurant.
     */
    @PostMapping("/{restaurantId}/orders/{orderId}/reject")
    public ResponseEntity<OrderDTO> rejectOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestBody RejectionRequestDTO rejectionRequest) {
        
        OrderDTO updatedOrder = orderService.rejectOrder(restaurantId, orderId, rejectionRequest.getReason());
        return ResponseEntity.ok(updatedOrder);
    }
}