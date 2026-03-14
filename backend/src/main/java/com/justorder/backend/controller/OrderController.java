package com.justorder.backend.controller;

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

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
        * Creates a new order from checkout data.
     *
        * Accepted payload rules:
        * customerId must be present, dishIds must contain valid positive ids,
        * clientTotal cannot be negative, and paymentToken cannot be blank.
        *
        * Validation is performed by {@code OrderService.checkout(...)} and returns:
        * 201 when the order is created, 400 for invalid payload/payment,
        * 404 when related entities do not exist, and 500 for unexpected errors.
        *
        * @param request checkout payload
        * @return HTTP response with created order or an error status
     */
    @PostMapping("/checkout")
    public ResponseEntity<OrderDTO> checkout(@RequestBody CheckoutOrderRequestDTO request) {
        try {
            OrderDTO createdOrder = orderService.checkout(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
