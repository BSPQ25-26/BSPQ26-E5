package com.justorder.backend.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.justorder.backend.dto.CheckoutOrderRequestDTO;
import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.exception.ResourceNotFoundException;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Dish;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.CustomerRepository;
import com.justorder.backend.repository.DishRepository;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RiderRepository;

@Service
/**
 * Handles order checkout business logic.
 */
public class OrderService {

    private static final String DEFAULT_ORDER_STATUS = "Pending";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final DishRepository dishRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final RiderRepository riderRepository;

    public OrderService(
        OrderRepository orderRepository,
        CustomerRepository customerRepository,
        DishRepository dishRepository,
        OrderStatusRepository orderStatusRepository,
        RiderRepository riderRepository
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.dishRepository = dishRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.riderRepository = riderRepository;
    }

    /**
     * Executes checkout: validates input, resolves entities, validates payment,
     * assigns default status and rider, and persists the order.
     *
     * @param request checkout request payload
     * @return created order as DTO
     * @throws IllegalArgumentException when request or payment data is invalid
     * @throws ResourceNotFoundException when required entities do not exist
     */
    @Transactional
    public OrderDTO checkout(CheckoutOrderRequestDTO request) {
        validateCheckoutRequest(request);

        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.getCustomerId()));

        List<Dish> dishes = dishRepository.findAllById(request.getDishIds());
        if (dishes.size() != request.getDishIds().size()) {
            throw new ResourceNotFoundException("One or more dishes do not exist");
        }

        double calculatedTotal = calculateTotal(dishes);
        validatePayment(request.getPaymentToken(), request.getClientTotal(), calculatedTotal);

        OrderStatus status = orderStatusRepository.findByStatusIgnoreCase(DEFAULT_ORDER_STATUS)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Order status not found: " + DEFAULT_ORDER_STATUS
            ));

        Rider assignedRider = riderRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(
                "No riders available to assign this order"
            ));

        Order order = new Order();
        order.setCustomer(customer);
        order.setDishes(dishes);
        order.setStatus(status);
        order.setRider(assignedRider);
        order.setTotalPrice(calculatedTotal);
        order.setSecretCode(generateSecretCode());

        return orderRepository.save(order).toDTO();
    }

    /**
     * Validates mandatory fields and basic constraints of a checkout request.
     *
     * @param request checkout request payload
     * @throws IllegalArgumentException when required fields are missing or invalid
     */
    private void validateCheckoutRequest(CheckoutOrderRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Checkout payload is required");
        }
        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("customerId is required");
        }
        if (request.getDishIds() == null || request.getDishIds().isEmpty()) {
            throw new IllegalArgumentException("dishIds must contain at least one dish");
        }
        if (request.getDishIds().stream().anyMatch(id -> id == null || id <= 0)) {
            throw new IllegalArgumentException("dishIds contains an invalid id");
        }
        if (request.getClientTotal() < 0) {
            throw new IllegalArgumentException("clientTotal cannot be negative");
        }
    }

    /**
     * Simulates payment validation for sprint scope.
     *
     * @param paymentToken token provided by client
     * @param clientTotal total sent by client
     * @param calculatedTotal total recalculated on server
     * @throws IllegalArgumentException when token is invalid or totals do not match
     */
    private void validatePayment(String paymentToken, double clientTotal, double calculatedTotal) {
        // Simulated payment check for Sprint 1.
        if (paymentToken == null || paymentToken.isBlank()) {
            throw new IllegalArgumentException("Invalid payment token");
        }

        if (Math.abs(clientTotal - calculatedTotal) > 0.01) {
            throw new IllegalArgumentException("Payment validation failed: total mismatch");
        }
    }

    /**
     * Computes total order price from the selected dishes.
     *
     * @param dishes selected dishes
     * @return sum of dish prices
     */
    private double calculateTotal(List<Dish> dishes) {
        return dishes.stream().mapToDouble(Dish::getPrice).sum();
    }

    /**
     * Generates a six-digit code used for delivery verification.
     *
     * @return random six-digit string
     */
    private String generateSecretCode() {
        int value = RANDOM.nextInt(900000) + 100000;
        return String.valueOf(value);
    }
}
