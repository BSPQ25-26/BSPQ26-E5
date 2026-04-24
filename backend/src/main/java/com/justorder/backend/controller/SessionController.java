package com.justorder.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.LoginRequest;
import com.justorder.backend.dto.LoginResponseDTO;
import com.justorder.backend.service.SessionService;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;    

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }
    
    @PostMapping("/users")
    public ResponseEntity<LoginResponseDTO> createUserSession(@RequestBody LoginRequest request) {
        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }

    @PostMapping("/riders")
    public ResponseEntity<LoginResponseDTO> createRiderSession(@RequestBody LoginRequest request) {
        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/restaurants")
    public ResponseEntity<LoginResponseDTO> createRestaurantSession(@RequestBody LoginRequest request) {
        try {
            LoginResponseDTO response = sessionService.createSession(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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
