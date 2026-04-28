package com.justorder.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminController {

    // Si un usuario normal intenta entrar aquí, Spring le devolverá un Error 403 (Forbidden)
    // Solo si el Token trae el "ROLE_ADMIN", podrá leer esto.
    @GetMapping("/dashboard")
    @Operation(summary = "Admin dashboard (requires admin role)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("¡Acceso concedido! Estás en el panel de control exclusivo para Administradores.");
    }
}