package com.justorder.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.dto.LoginResponseDTO;
import com.justorder.backend.service.SessionService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @brief Controller for managing user sessions (authentication).
 *
 * This controller handles login and logout operations for users,
 * riders, and restaurants.
 */
@RestController
@RequestMapping("/sessions")
@Tag(name = "Sessions")
public class SessionController {

    private final SessionService sessionService;
    private static final Logger logger = LogManager.getLogger(SessionController.class);

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * @brief Creates a session for a customer (login).
     *
     * @param request Login credentials.
     * @return Authentication response with session/token.
     */
    @PostMapping("/users")
    @Operation(summary = "Create a customer session (login)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session created"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    public ResponseEntity<LoginResponseDTO> createUserSession(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Login payload"
            )
            @RequestBody LoginRequest request) {

        logger.info("POST /sessions/users - create user session for {}",
                request != null ? request.getEmail() : "<null>");

        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.warn("Failed to create user session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * @brief Health check endpoint for session service.
     *
     * @return Simple message confirming service availability.
     */
    @GetMapping("/hello")
    @Operation(summary = "Health check for sessions")
    public String hello() {
        logger.info("GET /sessions/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }

    /**
     * @brief Creates a session for a rider (login).
     *
     * @param request Login credentials.
     * @return Authentication response with session/token.
     */
    @PostMapping("/riders")
    @Operation(summary = "Create a rider session (login)")
    public ResponseEntity<LoginResponseDTO> createRiderSession(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Login payload"
            )
            @RequestBody LoginRequest request) {

        logger.info("POST /sessions/riders - create rider session for {}",
                request != null ? request.getEmail() : "<null>");

        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.warn("Failed to create rider session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * @brief Creates a session for a restaurant (login).
     *
     * @param request Login credentials.
     * @return Authentication response with session/token.
     */
    @PostMapping("/restaurants")
    @Operation(summary = "Create a restaurant session (login)")
    public ResponseEntity<LoginResponseDTO> createRestaurantSession(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Login payload"
            )
            @RequestBody LoginRequest request) {

        logger.info("POST /sessions/restaurants - create restaurant session for {}",
                request != null ? request.getEmail() : "<null>");

        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.warn("Failed to create restaurant session", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * @brief Deletes a customer session (logout).
     *
     * @param token Authorization token.
     * @return HTTP 200 on successful logout.
     */
    @DeleteMapping("/users")
    @Operation(summary = "Delete customer session")
    public ResponseEntity<Void> deleteUserSession(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Authorization header: Bearer <token>"
            )
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("DELETE /sessions/users - delete user session");
        sessionService.deleteSession("customer", token);
        return ResponseEntity.ok().build();
    }

    /**
     * @brief Deletes a rider session (logout).
     *
     * @param token Authorization token.
     * @return HTTP 200 on successful logout.
     */
    @DeleteMapping("/riders")
    @Operation(summary = "Delete rider session")
    public ResponseEntity<Void> deleteRiderSession(
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("DELETE /sessions/riders - delete rider session");
        sessionService.deleteSession("rider", token);
        return ResponseEntity.ok().build();
    }

    /**
     * @brief Deletes a restaurant session (logout).
     *
     * @param token Authorization token.
     * @return HTTP 200 on successful logout.
     */
    @DeleteMapping("/restaurants")
    @Operation(summary = "Delete restaurant session")
    public ResponseEntity<Void> deleteRestaurantSession(
            @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("DELETE /sessions/restaurants - delete restaurant session");
        sessionService.deleteSession("restaurant", token);
        return ResponseEntity.ok().build();
    }
}