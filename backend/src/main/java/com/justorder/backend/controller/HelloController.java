package com.justorder.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @brief General controller for basic API health and test endpoints.
 *
 * This controller provides simple endpoints used to verify that
 * the backend service is running correctly.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "General", description = "Basic API health check and general test endpoints.")
public class HelloController {

    private static final Logger logger = LogManager.getLogger(HelloController.class);

    /**
     * @brief API health check endpoint.
     *
     * Simple endpoint used to verify that the API is running.
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