package com.justorder.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "General")
public class HelloController {

    private static final Logger logger = LogManager.getLogger(HelloController.class);

    @GetMapping("/hello")
    @Operation(summary = "API health check")
    public String hello() {
        logger.info("GET /api/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }
}
