package com.justorder.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Protected REST controller for exclusive Administrator operations.
 * Serves as a secure entry point that verifies the user has
 * the 'ROLE_ADMIN' role via their JWT token before allowing access.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * Retrieves the administrator dashboard.
     * If a regular user attempts to access this endpoint, Spring will return a 403 (Forbidden) error.
     * Only requests with a valid JWT containing the "ROLE_ADMIN" role can access this resource.
     * * @return ResponseEntity containing a welcome message for the administrator.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("¡Acceso concedido! Estás en el panel de control exclusivo para Administradores.");
    }
}