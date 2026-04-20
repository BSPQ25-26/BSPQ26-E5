package com.justorder.backend.service;

import java.util.HashMap;
import java.util.HashSet;

import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.dto.LoginResponseDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Restaurant;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.RestaurantRepository;
import com.justorder.backend.repository.RiderRepository;

public class SessionService {
    
    private HashMap<String, HashSet<String>> activeTokens = new HashMap<>();
    private final CustomerRepository customerRepository;
    private final RiderRepository riderRepository;
    private final RestaurantRepository restaurantRepository;
    
    public SessionService(CustomerRepository customerRepository, RiderRepository riderRepository, RestaurantRepository restaurantRepository) {
        this.customerRepository = customerRepository;
        this.riderRepository = riderRepository;
        this.restaurantRepository = restaurantRepository;
        activeTokens.put("rider", new HashSet<>());
        activeTokens.put("customer", new HashSet<>());
        activeTokens.put("restaurant", new HashSet<>());
    }

    private String generateToken(String type) {
        while (true) { // Bullshit way to ensure token uniqueness, but it works for this example
            String token = java.util.UUID.randomUUID().toString();
            if (!activeTokens.get(type).contains(token)) {
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
            if (rider != null && rider.getPassword().equals(request.getPassword())) {
                String token = generateToken(request.getType());
                activeTokens.get("rider").add(token);
                response.setToken(token);
                response.setRider(rider.toDTO());
                this.activeTokens.get("rider").add(token);
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
            if (restaurant != null && restaurant.getPassword().equals(request.getPassword())) {
                String token = generateToken(request.getType());
                activeTokens.get("restaurant").add(token);
                response.setToken(token);
                response.setRestaurant(restaurant.toDTO());
                this.activeTokens.get("restaurant").add(token);
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
            if (customer != null && customer.getPassword().equals(request.getPassword())) {
                String token = generateToken(request.getType());
                activeTokens.get("customer").add(token);
                response.setToken(token);
                response.setCustomer(customer.toDTO());
                this.activeTokens.get("customer").add(token);
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

