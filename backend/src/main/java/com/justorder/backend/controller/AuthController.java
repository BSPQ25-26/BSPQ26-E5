package com.justorder.backend.controller;

import com.justorder.backend.dto.AuthResponse;
import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.model.Admin;
import com.justorder.backend.repository.AdminRepository;
import com.justorder.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
 

import java.util.Optional;

/**
 * @brief Controller responsible for authentication operations.
 *
 * This controller handles admin authentication and JWT generation.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * @brief Authenticates an admin user and returns a JWT token.
     *
     * This endpoint verifies the provided credentials. If valid,
     * it generates and returns a JWT token with ADMIN role.
     *
     * @param request Login credentials containing email and password.
     * @return ResponseEntity containing a JWT token if authentication is successful,
     *         or HTTP 401 if credentials are invalid.
     */
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
            // 2. Comparamos la contraseña encriptada
            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                // 3. Si es correcta, generamos el token con el rol ROLE_ADMIN
                String token = jwtUtil.generateToken(admin.getEmail(), "ROLE_ADMIN");
                return ResponseEntity.ok(new AuthResponse(token));
            }
        }
        
        // Si falla el email o la contraseña, devolvemos error 401 (No autorizado)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }
}