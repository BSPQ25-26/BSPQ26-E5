package com.justorder.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for basic API health checks and greetings.
 * Serves as a simple endpoint to verify that the backend application 
 * is running and correctly responding to HTTP requests.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    /**
     * Handles GET requests to provide a basic greeting message.
     * Useful for quick connectivity tests from the frontend or external clients.
     * * @return a simple greeting {@link String} confirming the API is active.
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }
}