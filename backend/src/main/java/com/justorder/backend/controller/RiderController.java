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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

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
    @Operation(summary = "Health check for rider endpoints", description = "Simple hello endpoint to verify rider controller is reachable")
    public String hello() {
        return "Hello from JustOrder!";
    }

    /**
     * Retrieves all riders converted to DTOs.
     * Replacing the manual "nullification" of orders with clean DTO mapping.
     */
    @GetMapping
    @Operation(summary = "Get all riders")
    public ResponseEntity<List<RiderDTO>> getAllRiders() {
        List<RiderDTO> riders = riderRepository.findAll().stream()
                .map(Rider::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(riders);
    }

    /**
     * Registers a new rider using the specialized RegisterService.
     */
    @Operation(summary = "Create or update a rider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rider created/updated successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createOrUpdateRider(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Rider payload") @RequestBody RiderDTO request) {
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
    @Operation(summary = "Update an existing rider")
    @PutMapping("/{id}")
    public ResponseEntity<RiderDTO> updateRider(@Parameter(description = "ID of the rider") @PathVariable Long id, @RequestBody RiderDTO request) {
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
    @Operation(summary = "Delete a rider by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRider(@Parameter(description = "ID of the rider") @PathVariable Long id) {
        if (riderRepository.existsById(id)) {
            riderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Updates status of orders (placeholder from main).
     */
    @Operation(summary = "Update rider order status (placeholder)")
    @PostMapping("/orders")
    public ResponseEntity<HttpStatus> updateStatus(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Status payload") @RequestBody String request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Retrieves a specific rider by ID.
     */
    @Operation(summary = "Get rider by id", description = "Returns rider profile for a given rider id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rider found"),
        @ApiResponse(responseCode = "404", description = "Rider not found")
    })
    @GetMapping("/{riderId}")
    public ResponseEntity<RiderDTO> getRider(@Parameter(description = "ID of the rider") @PathVariable Long riderId) {
        try {
            return ResponseEntity.ok(riderService.getRider(riderId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Retrieves orders assigned to a specific rider.
     */
    @Operation(summary = "Get orders assigned to a rider")
    @GetMapping("/{riderId}/orders")
    public ResponseEntity<List<OrderDTO>> getRiderOrders(@Parameter(description = "ID of the rider") @PathVariable Long riderId) {
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
    @Operation(summary = "Get rider dashboard", description = "Returns dashboard stats and assigned orders for the authenticated rider (Authorization header required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Rider not found")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<RiderDashboardDTO> getRiderDashboard(@Parameter(description = "Authorization header: Bearer <token>") @RequestHeader("Authorization") String authorization) {
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
    @Operation(summary = "Reject an assigned order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order rejected"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/{riderId}/orders/{orderId}/reject")
    public ResponseEntity<OrderDTO> rejectOrder(
            @Parameter(description = "ID of the rider") @PathVariable Long riderId,
            @Parameter(description = "ID of the order") @PathVariable Long orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Reason for rejection") @RequestBody Map<String, String> body) {

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
    @Operation(summary = "Verify order PIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PIN verified"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "423", description = "Locked - invalid state")
    })
    @PostMapping("/{riderId}/orders/{orderId}/verify-pin")
    public ResponseEntity<OrderDTO> verifyOrderPin(
            @Parameter(description = "ID of the rider") @PathVariable Long riderId,
            @Parameter(description = "ID of the order") @PathVariable Long orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "PIN payload") @RequestBody VerifyOrderPinRequestDTO request) {

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
    @Operation(summary = "Delete all riders")
    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllRiders() {
        riderRepository.deleteAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}