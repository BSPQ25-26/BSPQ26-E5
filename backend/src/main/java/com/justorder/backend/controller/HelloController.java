package com.justorder.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for basic API health checks and greetings.
 * Serves as a simple endpoint to verify that the backend application 
 * is running and correctly responding to HTTP requests.
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@Tag(name = "General")
public class HelloController {

    private static final Logger logger = LogManager.getLogger(HelloController.class);

    /**
     * Handles GET requests to provide a basic greeting message.
     * Useful for quick connectivity tests from the frontend or external clients.
     * @return a simple greeting {@link String} confirming the API is active.
     */
    @GetMapping("/hello")
    @Operation(summary = "API health check")
    public String hello() {
        logger.info("GET /api/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }
}