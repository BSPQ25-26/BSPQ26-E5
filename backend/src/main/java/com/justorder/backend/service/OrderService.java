package com.justorder.backend.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Creates an order from checkout data after validating the payload,
     * recalculating the total, and resolving the required related entities.
     *
     * Accepted payload: existing customerId, at least one existing dishId,
     * non-negative clientTotal, and a non-blank paymentToken.
     */
    @Transactional
    public OrderDTO checkout(CheckoutOrderRequestDTO request) {
        validateCheckoutRequest(request);

        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.getCustomerId()));

        List<Dish> dishes = resolveRequestedDishes(request.getDishIds());

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
        * Validates mandatory checkout fields and basic constraints.
     *
     * @param request checkout request payload
        * @throws IllegalArgumentException when request is null, customerId is missing,
        *         dishIds is empty/invalid, or clientTotal is negative
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
     * Performs the sprint payment check by ensuring the token is present and
     * the client total matches the server-side recalculated total.
     */
    private void validatePayment(String paymentToken, double clientTotal, double calculatedTotal) {
        if (paymentToken == null || paymentToken.isBlank()) {
            throw new IllegalArgumentException("Invalid payment token");
        }

        if (Math.abs(clientTotal - calculatedTotal) > 0.01) {
            throw new IllegalArgumentException("Payment validation failed: total mismatch");
        }
    }

    private double calculateTotal(List<Dish> dishes) {
        return dishes.stream().mapToDouble(Dish::getPrice).sum();
    }

    private List<Dish> resolveRequestedDishes(List<Long> dishIds) {
        Map<Long, Dish> dishesById = new HashMap<>();
        dishRepository.findAllById(dishIds).forEach(dish -> dishesById.put(dish.getId(), dish));

        List<Dish> resolvedDishes = dishIds.stream()
            .map(dishId -> {
                Dish dish = dishesById.get(dishId);
                if (dish == null) {
                    throw new ResourceNotFoundException("Dish not found: " + dishId);
                }
                return dish;
            })
            .toList();

        if (resolvedDishes.isEmpty()) {
            throw new ResourceNotFoundException("One or more dishes do not exist");
        }

        return resolvedDishes;
    }

    private String generateSecretCode() {
        int value = RANDOM.nextInt(900000) + 100000;
        return String.valueOf(value);
    }
}
