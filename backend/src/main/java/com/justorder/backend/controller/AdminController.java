package com.justorder.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST protegido para operaciones exclusivas del Administrador.
 * Sirve como punto de entrada seguro que verifica que el usuario tiene
 * el rol 'ROLE_ADMIN' mediante su token JWT antes de permitir el acceso.
 * @version 1.0
 */

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // Si un usuario normal intenta entrar aquí, Spring le devolverá un Error 403 (Forbidden)
    // Solo si el Token trae el "ROLE_ADMIN", podrá leer esto.
    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("¡Acceso concedido! Estás en el panel de control exclusivo para Administradores.");
    }
}