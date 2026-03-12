package com.justorder.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    
    @PostMapping("/users")
    public ResponseEntity<String> createUserSession() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented");
    }

    @PostMapping("/riders")
    public ResponseEntity<String> createRiderSession() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented"); 
    }

    @PostMapping("/restaurants")
    public ResponseEntity<String> createRestaurantSession() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not implemented");
    }

    @DeleteMapping("/users")
    public HttpStatus deleteUserSession() {
        return HttpStatus.NOT_IMPLEMENTED;
    }

    @DeleteMapping("/riders")
    public HttpStatus deleteRiderSession() {
        return HttpStatus.NOT_IMPLEMENTED;
    }

    @DeleteMapping("/restaurants")
    public HttpStatus deleteRestaurantSession() {
        return HttpStatus.NOT_IMPLEMENTED;
    }
    
}
