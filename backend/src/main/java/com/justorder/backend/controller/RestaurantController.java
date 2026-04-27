package com.justorder.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;

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


@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private static final Logger logger = LogManager.getLogger(RestaurantController.class);

    @Autowired
    private MenuService menuService;
    private final RegisterService registerService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantService restaurantService;
    private final SessionService sessionService;


    private final OrderService orderService;

    /***
     * @param registerService      
     * @param restaurantRepository 
     * @param restaurantService    
     */
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

    @GetMapping("/hello")
    public String hello() {
        logger.info("GET /api/restaurants/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateRestaurant(@RequestBody RestaurantDTO request) {
        try {
            logger.info("POST /api/restaurants/create - register restaurant request: {}", request != null ? request.getName() : "<null>");
            this.registerService.registerRestaurant(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error registering restaurant", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/menu")
    public ResponseEntity<HttpStatus> createOrUpdateMenu(@RequestBody List<DishDTO> request) {
   
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
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

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable String restaurantId) {
   
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<DishDTO>> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenu(restaurantId));
    }

    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllRestaurants() {
        restaurantRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<RestaurantDTO> results = restaurantService.searchRestaurants(
                cuisine, minRating, minPrice, maxPrice
        );
        return ResponseEntity.ok(results);
    }
    @PostMapping("/{restaurantId}/orders/{orderId}/reject")
    public ResponseEntity<OrderDTO> rejectOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestBody RejectionRequestDTO rejectionRequest) {
        
        OrderDTO updatedOrder = orderService.rejectOrder(restaurantId, orderId, rejectionRequest.getReason());
        return ResponseEntity.ok(updatedOrder);
    }
}