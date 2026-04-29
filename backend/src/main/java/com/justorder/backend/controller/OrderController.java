package com.justorder.backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.CheckoutOrderRequestDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Accepts checkout payloads with customerId, dishIds, clientTotal and paymentToken.
     * Returns 201 when the order is created, 400 for invalid payload or payment data,
     * and 404 when referenced entities or an assignable rider are missing.
     */
    @Operation(summary = "Create an order (checkout)", description = "Creates an order from checkout payload; returns created order with id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Checkout payload") @RequestBody CheckoutOrderRequestDTO request) {
        logger.info("POST /api/orders/checkout - checkout requested for customer {}", request != null ? request.getCustomerId() : "<null>");
        try {
            OrderDTO createdOrder = orderService.checkout(request);
            logger.info("Order created with id {}", createdOrder != null ? createdOrder.getId() : "<null>");
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
