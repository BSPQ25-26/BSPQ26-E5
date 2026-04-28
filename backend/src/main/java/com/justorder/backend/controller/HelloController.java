package com.justorder.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    private static final Logger logger = LogManager.getLogger(HelloController.class);

    @GetMapping("/hello")
    public String hello() {
        logger.info("GET /api/hello - hello endpoint called");
        return "Hello from JustOrder!";
    }
}
