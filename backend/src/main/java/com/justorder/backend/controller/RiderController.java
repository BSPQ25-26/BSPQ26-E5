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
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @brief Controller for managing rider-related operations.
 *
 * This controller handles rider registration, order assignment,
 * delivery workflows, administrative operations, and rider dashboard information.
 */
@RestController
@RequestMapping("/api/riders")
@Tag(name = "Riders", description = "Endpoints for rider registration, order assignment, and delivery workflows.")
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

    /**
     * @brief Health check endpoint for rider API.
     *
     * @return Simple message confirming service availability.
     */
    @GetMapping("/hello")
    @Operation(summary = "Health check for rider endpoints", description = "Simple hello endpoint to verify rider controller is reachable")
    public String hello() {
        return "Hello from JustOrder!";
    }

    /**
     * @brief Retrieves all riders.
     * * Converts entities to DTOs.
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
     * @brief Creates or updates a rider.
     *
     * @param request Rider data transfer object.
     * @return HTTP 201 if successful, otherwise HTTP 500.
     */
    @PostMapping("/create")
    @Operation(summary = "Create or update a rider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rider created/updated successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<HttpStatus> createOrUpdateRider(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Rider payload"
            )
            @RequestBody RiderDTO request) {

        try {
            this.registerService.registerRider(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Updates an existing rider's profile.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing rider")
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
     * @brief Deletes a specific rider by ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a rider by id")
    public ResponseEntity<Void> deleteRider(@Parameter(description = "ID of the rider") @PathVariable Long id) {
        if (riderRepository.existsById(id)) {
            riderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * @brief Retrieves a rider by ID.
     *
     * @param riderId Rider identifier.
     * @return Rider data or HTTP 404 if not found.
     */
    @GetMapping("/{riderId}")
    @Operation(summary = "Get rider by id", description = "Returns rider profile for a given rider id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rider found"),
        @ApiResponse(responseCode = "404", description = "Rider not found")
    })
    public ResponseEntity<RiderDTO> getRider(
            @Parameter(description = "ID of the rider")
            @PathVariable Long riderId) {

        try {
            return ResponseEntity.ok(riderService.getRider(riderId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * @brief Retrieves orders assigned to a rider.
     *
     * @param riderId Rider identifier.
     * @return List of assigned orders.
     */
    @GetMapping("/{riderId}/orders")
    @Operation(summary = "Get orders assigned to a rider")
    public ResponseEntity<List<OrderDTO>> getRiderOrders(
            @Parameter(description = "ID of the rider")
            @PathVariable Long riderId) {

        try {
            List<OrderDTO> orders = riderService.getRiderOrders(riderId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * @brief Retrieves unassigned available orders.
     *
     * @return List of unassigned orders.
     */
    @GetMapping("/orders/available")
    @Operation(summary = "Get unassigned orders available for riders")
    public ResponseEntity<List<OrderDTO>> getAvailableOrders() {
        return ResponseEntity.ok(riderService.getAvailableOrders());
    }

    /**
     * @brief Assigns an order to a rider.
     *
     * @param riderId Rider identifier.
     * @param orderId Order identifier.
     * @param request Order payload.
     * @return Updated order or error status.
     */
    @PostMapping("/{riderId}/orders/{orderId}")
    @Operation(summary = "Assign an available order to a rider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order assigned"),
        @ApiResponse(responseCode = "400", description = "Bad request (e.g., already assigned)"),
        @ApiResponse(responseCode = "404", description = "Rider or order not found")
    })
    public ResponseEntity<OrderDTO> assignOrder(
            @Parameter(description = "ID of the rider") @PathVariable Long riderId,
            @Parameter(description = "ID of the order") @PathVariable Long orderId,
            @RequestBody OrderDTO request) {

        try {
            OrderDTO updatedOrder = riderService.assignOrder(riderId, orderId, request);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Updates rider order status.
     *
     * @param riderId Rider identifier.
     * @param orderId Order identifier.
     * @param body Payload containing new status.
     * @return Updated order or error status.
     */
    @PostMapping("/{riderId}/orders/{orderId}/status")
    @Operation(summary = "Update assigned order status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated"),
        @ApiResponse(responseCode = "400", description = "Bad request (e.g., trying to set Delivered without PIN)"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Parameter(description = "ID of the rider") @PathVariable Long riderId,
            @Parameter(description = "ID of the order") @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        if (status == null || status.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            OrderDTO updatedOrder = riderService.updateOrderStatus(riderId, orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Retrieves rider dashboard data.
     *
     * @param authorization JWT authorization header.
     * @return Dashboard information or error status.
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get rider dashboard", description = "Returns dashboard stats and assigned orders for the authenticated rider (Authorization header required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Rider not found")
    })
    public ResponseEntity<RiderDashboardDTO> getRiderDashboard(
            @Parameter(description = "Authorization header: Bearer <token>")
            @RequestHeader("Authorization") String authorization) {

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
     * @brief Rejects an assigned order.
     *
     * @param riderId Rider identifier.
     * @param orderId Order identifier.
     * @param body Request body containing rejection reason.
     * @return Updated order or error status.
     */
    @PostMapping("/{riderId}/orders/{orderId}/reject")
    @Operation(summary = "Reject an assigned order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order rejected"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<OrderDTO> rejectOrder(
            @Parameter(description = "ID of the rider") @PathVariable Long riderId,
            @Parameter(description = "ID of the order") @PathVariable Long orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Reason for rejection"
            )
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
     * @brief Verifies the delivery PIN for an order.
     *
     * @param riderId Rider identifier.
     * @param orderId Order identifier.
     * @param request PIN verification payload.
     * @return Updated order if PIN is correct.
     */
    @PostMapping("/{riderId}/orders/{orderId}/verify-pin")
    @Operation(summary = "Verify order PIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PIN verified"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "423", description = "Locked - invalid state")
    })
    public ResponseEntity<OrderDTO> verifyOrderPin(
            @Parameter(description = "ID of the rider") @PathVariable Long riderId,
            @Parameter(description = "ID of the order") @PathVariable Long orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "PIN payload"
            )
            @RequestBody VerifyOrderPinRequestDTO request) {

        if (request == null || request.getPin() == null || request.getPin().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            OrderDTO updatedOrder =
                    riderService.verifyOrderPin(riderId, orderId, request.getPin());

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
     * @brief Deletes all riders from the system.
     *
     * WARNING: This operation removes all rider data.
     *
     * @return HTTP 200 when deletion is completed.
     */
    @DeleteMapping()
    @Operation(summary = "Delete all riders")
    public ResponseEntity<HttpStatus> deleteAllRiders() {
        riderRepository.deleteAll();
        return ResponseEntity.ok(HttpStatus.OK);
    }
}