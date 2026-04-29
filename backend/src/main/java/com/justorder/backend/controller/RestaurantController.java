package com.justorder.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.justorder.backend.dto.*;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.service.*;

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
        return "Hello from JustOrder!";
    }

    @GetMapping
    @Operation(summary = "Get all restaurants")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<RestaurantDTO> results = restaurantRepository.findAll().stream()
                .map(Restaurant::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @PostMapping("/create")
    @Operation(summary = "Create a restaurant")
    public ResponseEntity<HttpStatus> createOrUpdateRestaurant(@RequestBody RestaurantDTO request) {
        try {
            this.registerService.registerRestaurant(request);
            // SOLUCIÓN ERROR 201 vs 200: Forzamos OK (200) porque el test espera isOk()
            return ResponseEntity.ok().build(); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete all restaurants")
    // SOLUCIÓN ERROR 405: Eliminamos el "/all" para que coincida con el test que llama a DELETE /api/restaurants
    public ResponseEntity<HttpStatus> deleteAllRestaurants() {
        restaurantRepository.deleteAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<RestaurantDTO> getMyProfile(@RequestHeader("Authorization") String authorization) {
        try {
            Long restaurantId = sessionService.getActiveRestaurantId(authorization);
            return ResponseEntity.ok(restaurantService.getRestaurantProfile(restaurantId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<RestaurantDTO> updateMyProfile(@RequestHeader("Authorization") String authorization, @RequestBody RestaurantProfileUpdateDTO request) {
        try {
            Long restaurantId = sessionService.getActiveRestaurantId(authorization);
            return ResponseEntity.ok(restaurantService.updateRestaurantProfile(restaurantId, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RestaurantDashboardDTO> getMyDashboard(@RequestHeader("Authorization") String authorization) {
        try {
            Long restaurantId = sessionService.getActiveRiderId(authorization); // Usamos el ID activo
            return ResponseEntity.ok(restaurantService.getRestaurantDashboard(restaurantId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(cuisine, minRating, minPrice, maxPrice));
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<DishDTO>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO request) {
        return restaurantRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            return ResponseEntity.ok(restaurantRepository.save(existing).toDTO());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{restaurantId}/orders/{orderId}/reject")
    public ResponseEntity<OrderDTO> rejectOrder(
            @PathVariable Long restaurantId, 
            @PathVariable Long orderId,
            @RequestBody RejectionRequestDTO rejectionRequest) {
        return ResponseEntity.ok(orderService.rejectOrder(restaurantId, orderId, rejectionRequest.getReason()));
    }
}