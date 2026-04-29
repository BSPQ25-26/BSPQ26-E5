package com.justorder.backend.controller;

import com.justorder.backend.dto.AuthResponse;
import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.model.Admin;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller responsible for authentication and security.
 * Manages the login process by validating credentials against the database
 * and generates a signed JWT token to securely maintain active sessions.
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/admin/login")
    @Operation(summary = "Admin login", description = "Authenticate admin and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authenticated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> loginAdmin(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials") @RequestBody LoginRequest request) {
        // 1. Buscamos al admin por su email
        Optional<Admin> adminOptional = adminRepository.findByEmail(request.getEmail());

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                String token = jwtUtil.generateToken(admin.getEmail(), "ROLE_ADMIN");
                return ResponseEntity.ok(new AuthResponse(token));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
    }

    @PostMapping("/customer/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest request) {
        Optional<Customer> customerOptional = customerRepository.findByEmail(request.getEmail());

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                String token = jwtUtil.generateToken(customer.getEmail(), "ROLE_CUSTOMER");
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("token", token);
                responseBody.put("id", customer.getId());
                return ResponseEntity.ok(responseBody);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
    }

    @PostMapping("/rider/login")
    public ResponseEntity<?> loginRider(@RequestBody LoginRequest request) {
        Optional<Rider> riderOptional = riderRepository.findByEmail(request.getEmail());

        if (riderOptional.isPresent()) {
            Rider rider = riderOptional.get();
            if (passwordEncoder.matches(request.getPassword(), rider.getPassword())) {
                String token = jwtUtil.generateToken(rider.getEmail(), "ROLE_RIDER");
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("token", token);
                responseBody.put("id", rider.getId());
                return ResponseEntity.ok(responseBody);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
    }

    @PostMapping("/restaurant/login")
    public ResponseEntity<?> loginRestaurant(@RequestBody LoginRequest request) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findByEmail(request.getEmail());

        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();
            if (passwordEncoder.matches(request.getPassword(), restaurant.getPassword())) {
                String token = jwtUtil.generateToken(restaurant.getEmail(), "ROLE_RESTAURANT");
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("token", token);
                responseBody.put("id", restaurant.getId());
                return ResponseEntity.ok(responseBody);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
    }
}