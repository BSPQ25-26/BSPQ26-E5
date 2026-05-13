package com.justorder.backend.controller;

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
 * This controller handles order creation through checkout requests.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Endpoints to create and manage orders through checkout operations.")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * @brief Creates an order from a checkout request.
     *
     * This endpoint processes a checkout request containing customer,
     * dishes, and payment information, and creates a new order.
     *
     * @param request Checkout order request data.
     * @return The created order with HTTP 201 status,
     *         or an error status depending on validation or processing results.
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
}