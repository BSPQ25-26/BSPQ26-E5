package com.justorder.backend.controller;

import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.dto.RiderDashboardDTO;
import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.dto.VerifyOrderPinRequestDTO;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.RiderRepository;
import com.justorder.backend.service.RegisterService;
import com.justorder.backend.service.RiderService;
import com.justorder.backend.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing Rider (delivery driver) entities.
 * Handles administrative operations, registration, and order management for riders.
 */
@RestController
@RequestMapping("/api/riders")
public class RiderController {

    private final RegisterService registerService;
    private final RiderRepository riderRepository;
    private final RiderService riderService;
    private final SessionService sessionService;

    /**
     * Constructor injection for improved testability and architecture.
     */
    public RiderController(RegisterService registerService,
                           RiderRepository riderRepository,
                           RiderService riderService,
                           SessionService sessionService) {
        this.registerService = registerService;
        this.riderRepository = riderRepository;
        this.riderService = riderService;
        this.sessionService = sessionService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JustOrder!";
    }

    /**
     * Retrieves all riders converted to DTOs.
     * Replacing the manual "nullification" of orders with clean DTO mapping.
     */
    @GetMapping
    public ResponseEntity<List<RiderDTO>> getAllRiders() {
        List<RiderDTO> riders = riderRepository.findAll().stream()
                .map(Rider::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(riders);
    }

    /**
     * Registers a new rider using the specialized RegisterService.
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createRider(@RequestBody RiderDTO request) {
        try {
            this.registerService.registerRider(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing rider's profile.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RiderDTO> updateRider(@PathVariable Long id, @RequestBody RiderDTO request) {
        return riderRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setEmail(request.getEmail());
            existing.setPhoneNumber(request.getPhoneNumber());

            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existing.setPassword(request.getPassword());
            }

            Rider updated = riderRepository.save(existing);
            return ResponseEntity.ok(updated.toDTO());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific rider by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRider(@PathVariable Long id) {
        if (riderRepository.existsById(id)) {
            riderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Updates status of orders (placeholder from main).
     */
    @PostMapping("/orders")
    public ResponseEntity<HttpStatus> updateStatus(@RequestBody String request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Retrieves a specific rider by ID (placeholder from main).
     */
    @GetMapping("/{riderId}")
    public ResponseEntity<RiderDTO> getRider(@PathVariable Long riderId) {
        try {
            return ResponseEntity.ok(riderService.getRider(riderId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Retrieves orders assigned to a specific rider.
     */
    @GetMapping("/{riderId}/orders")
    public ResponseEntity<List<OrderDTO>> getRiderOrders(@PathVariable Long riderId) {
        try {
            List<OrderDTO> orders = riderService.getRiderOrders(riderId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Retrieves the dashboard metrics for a specific rider.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<RiderDashboardDTO> getRiderDashboard(@RequestHeader("Authorization") String authorization) {
        try {
            Long riderId = sessionService.getActiveRiderId(authorization);
            return ResponseEntity.ok(riderService.getRiderDashboard(riderId));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Logic for a rider to reject an assigned order with a reason.
     */
    @PostMapping("/{riderId}/orders/{orderId}/reject")
    public ResponseEntity<OrderDTO> rejectOrder(
            @PathVariable Long riderId,
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {

        String reason = body.get("reason");
        if (reason == null || reason.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            OrderDTO updatedOrder = riderService.rejectOrder(riderId, orderId, reason);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Logic for a rider to verify an order PIN upon delivery.
     */
    @PostMapping("/{riderId}/orders/{orderId}/verify-pin")
    public ResponseEntity<OrderDTO> verifyOrderPin(
            @PathVariable Long riderId,
            @PathVariable Long orderId,
            @RequestBody VerifyOrderPinRequestDTO request) {

        if (request == null || request.getPin() == null || request.getPin().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            OrderDTO updatedOrder = riderService.verifyOrderPin(riderId, orderId, request.getPin());
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Bulk deletion of all riders (Admin/Testing utility).
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllRiders() {
        riderRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}