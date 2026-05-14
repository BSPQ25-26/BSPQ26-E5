package com.justorder.backend.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.CheckoutOrderRequestDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @brief Controller responsible for order management operations.
 *
 * This controller handles order creation through checkout requests,
 * as well as administrative operations like retrieving, updating,
 * and deleting orders.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Endpoints to create and manage orders through checkout and admin operations.")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    private final OrderService orderService;

    /**
     * @brief Constructor injection for the OrderService.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * @brief Retrieves a list of all orders in the database.
     *
     * @return a ResponseEntity containing a list of {@link OrderDTO} objects.
     */
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieves all orders in the system.")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * @brief Creates an order from a checkout request.
     *
     * This endpoint processes a checkout request containing customer,
     * dishes, and payment information, and creates a new order.
     *
     * @param request Checkout order request data.
     * @return The created order with HTTP 201 status,
     * or an error status depending on validation or processing results.
     */
    @PostMapping("/checkout")
    @Operation(summary = "Create an order (checkout)", description = "Creates an order from checkout payload; returns created order with id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Referenced resource not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDTO> checkout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Checkout payload"
            )
            @RequestBody CheckoutOrderRequestDTO request) {

        logger.info("POST /api/orders/checkout - checkout requested for customer {}",
                request != null ? request.getCustomerId() : "<null>");

        try {
            OrderDTO createdOrder = orderService.checkout(request);

            logger.info("Order created with id {}",
                    createdOrder != null ? createdOrder.getId() : "<null>");

            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid checkout request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during checkout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            logger.error("Error during checkout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @brief Administrative endpoint to create an order manually.
     */
    @PostMapping
    @Operation(summary = "Create order manually", description = "Admin endpoint to directly create an order without checkout.")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    /**
     * @brief Updates an existing order's details and status.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an order", description = "Updates an existing order's details and status.")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO request) {
        try {
            return ResponseEntity.ok(orderService.updateOrder(id, request));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * @brief Deletes a specific order by ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order", description = "Removes an order from the database by its ID.")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}