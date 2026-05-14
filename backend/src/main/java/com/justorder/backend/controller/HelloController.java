package com.justorder.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @brief General controller for basic API health and test endpoints.
 *
 * Serves as a simple endpoint to verify that the backend application 
 * is running and correctly responding to HTTP requests.
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@Tag(name = "General", description = "Basic API health check and general test endpoints.")
public class HelloController {

    private static final Logger logger = LogManager.getLogger(HelloController.class);

    /**
     * @brief API health check endpoint.
     *
     * Handles GET requests to provide a basic greeting message.
     * Useful for quick connectivity tests from the frontend or external clients.
     *
     * @return A greeting message confirming service availability.
     */
    @GetMapping("/hello")
    @Operation(summary = "API health check")
    public String hello() {
        logger.info("GET /api/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }
}