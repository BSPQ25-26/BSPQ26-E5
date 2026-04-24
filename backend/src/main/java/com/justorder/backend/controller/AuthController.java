package com.justorder.backend.controller;

import com.justorder.backend.dto.AuthResponse;
import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.model.Admin;
import com.justorder.backend.model.Customer;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authenticates an administrator based on the provided credentials.
     * @param request the data transfer object containing the administrator's email and password.
     * @return a ResponseEntity containing an {@link AuthResponse} with the generated JWT token 
     * and an HTTP 200 OK status if successful; otherwise, returns an HTTP 401 Unauthorized 
     * status with an error message if the credentials are invalid.
     */
    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) {
        // 1. Find the admin by email
        Optional<Admin> adminOptional = adminRepository.findByEmail(request.getEmail());

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            // 2. Compare the provided password with the encrypted one in the database
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                // 3. If valid, generate the token with the ROLE_ADMIN role
                String token = jwtUtil.generateToken(admin.getEmail(), "ROLE_ADMIN");
                return ResponseEntity.ok(new AuthResponse(token));
            }
        }
        
        // If email or password validation fails, return a 401 Unauthorized error
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
    }

    /**
     * Authenticates a customer based on the provided credentials.
     */
    @PostMapping("/customer/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest request) {
        Optional<Customer> customerOptional = customerRepository.findByEmail(request.getEmail());

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            
            // Compare the provided password with the stored encrypted password
            if (passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                
                // Generate a real token with the CUSTOMER role
                String token = jwtUtil.generateToken(customer.getEmail(), "ROLE_CUSTOMER");
                
                // Return the token and the customer's real ID in a JSON response
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("token", token);
                responseBody.put("id", customer.getId());
                
                return ResponseEntity.ok(responseBody);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
    }
}