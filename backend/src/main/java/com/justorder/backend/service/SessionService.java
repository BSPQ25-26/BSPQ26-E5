package com.justorder.backend.service;

import java.util.HashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.dto.LoginResponseDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.RiderRepository;

@Service
public class SessionService {
    
    private HashMap<String, HashMap<String, Long>> activeTokens = new HashMap<>();
    private final CustomerRepository customerRepository;
    private final RiderRepository riderRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    
    public SessionService(CustomerRepository customerRepository, RiderRepository riderRepository, RestaurantRepository restaurantRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.riderRepository = riderRepository;
        this.restaurantRepository = restaurantRepository;
        this.passwordEncoder = passwordEncoder;
        activeTokens.put("rider", new HashMap<>());
        activeTokens.put("customer", new HashMap<>());
        activeTokens.put("restaurant", new HashMap<>());
    }

    public Long getActiveRestaurantId(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        if (token == null || token.isBlank()) {
            throw new SecurityException("Authorization token is required");
        }

        Long restaurantId = activeTokens.get("restaurant").get(token);
        if (restaurantId == null) {
            throw new SecurityException("Invalid or expired restaurant token");
        }

        if (!restaurantRepository.existsById(restaurantId)) {
            throw new SecurityException("Restaurant not found for token");
        }

        return restaurantId;
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }

        String trimmedHeader = authorizationHeader.trim();
        if (trimmedHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmedHeader.substring(7).trim();
        }

        return trimmedHeader;
    }

    private String generateToken(String type) {
        while (true) { // Bullshit way to ensure token uniqueness, but it works for this example
            String token = java.util.UUID.randomUUID().toString();
            if (!activeTokens.get(type).containsKey(token)) {
                return token;
            }
        }
    }

    public LoginResponseDTO createSession(LoginRequest request) {
        LoginResponseDTO response = new LoginResponseDTO();
        // Implementation for creating a session
        switch (request.getType()) {
            case "customer" -> createCustomerSession(request, response);
            case "rider" -> createRiderSession(request, response);
            case "restaurant" -> createRestaurantSession(request, response);
            default -> throw new RuntimeException("Invalid user type");
        }
        return response;
    }

    private void createRiderSession(LoginRequest request, LoginResponseDTO response) {
        // Validate rider credentials and create session
        if (riderRepository.existsByEmail(request.getEmail())) {
            Rider rider = riderRepository.findByEmail(request.getEmail());
            if (rider != null && passwordEncoder.matches(request.getPassword(), rider.getPassword())) {
                String token = generateToken(request.getType());
                activeTokens.get("rider").put(token, rider.getId());
                response.setToken(token);
                response.setRider(rider.toDTO());
            }
            else {
                throw new RuntimeException("Invalid credentials");
            }
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    private void createRestaurantSession(LoginRequest request, LoginResponseDTO response) {
        // Validate restaurant credentials and create session
        if (restaurantRepository.existsByEmail(request.getEmail())) {
            Restaurant restaurant = restaurantRepository.findByEmail(request.getEmail());
            if (restaurant != null && passwordEncoder.matches(request.getPassword(), restaurant.getPassword())) {
                String token = generateToken(request.getType());
                activeTokens.get("restaurant").put(token, restaurant.getId());
                response.setToken(token);
                response.setRestaurant(restaurant.toDTO());
            }
            else {
                throw new RuntimeException("Invalid credentials");
            }
        }
        else {
            throw new RuntimeException("User not found");
        }
    }

    private void createCustomerSession(LoginRequest request, LoginResponseDTO response) {
        // Validate customer credentials and create session
        if (customerRepository.existsByEmail(request.getEmail())) {
            Customer customer = customerRepository.findByEmail(request.getEmail());
            if (customer != null && passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                String token = generateToken(request.getType());
                activeTokens.get("customer").put(token, customer.getId());
                response.setToken(token);
                response.setCustomer(customer.toDTO());
            }
            else {
                throw new RuntimeException("Invalid credentials");
            }
        }
        else {
            throw new RuntimeException("User not found");
        }
    }
}

