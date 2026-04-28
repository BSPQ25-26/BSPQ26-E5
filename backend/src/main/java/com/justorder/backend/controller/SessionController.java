package com.justorder.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.dto.LoginResponseDTO;
import com.justorder.backend.service.SessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;    

    private static final Logger logger = LogManager.getLogger(SessionController.class);

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }
    
    @PostMapping("/users")
    public ResponseEntity<LoginResponseDTO> createUserSession(@RequestBody LoginRequest request) {
        logger.info("POST /sessions/users - create user session for {}", request != null ? request.getEmail() : "<null>");
        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Failed to create user session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/hello")
    public String hello() {
        logger.info("GET /sessions/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }

    @PostMapping("/riders")
    public ResponseEntity<LoginResponseDTO> createRiderSession(@RequestBody LoginRequest request) {
        logger.info("POST /sessions/riders - create rider session for {}", request != null ? request.getEmail() : "<null>");
        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Failed to create rider session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/restaurants")
    public ResponseEntity<LoginResponseDTO> createRestaurantSession(@RequestBody LoginRequest request) {
        logger.info("POST /sessions/restaurants - create restaurant session for {}", request != null ? request.getEmail() : "<null>");
        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Failed to create restaurant session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUserSession(@RequestHeader(value = "Authorization", required = false) String token) {
        logger.info("DELETE /sessions/users - delete user session");
        sessionService.deleteSession("customer", token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/riders")
    public ResponseEntity<Void> deleteRiderSession(@RequestHeader(value = "Authorization", required = false) String token) {
        logger.info("DELETE /sessions/riders - delete rider session");
        sessionService.deleteSession("rider", token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/restaurants")
    public ResponseEntity<Void> deleteRestaurantSession(@RequestHeader(value = "Authorization", required = false) String token) {
        logger.info("DELETE /sessions/restaurants - delete restaurant session");
        sessionService.deleteSession("restaurant", token);
        return ResponseEntity.ok().build();
    }
    
}
