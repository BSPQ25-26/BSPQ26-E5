package com.justorder.backend.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

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
     * Retrieves all orders for the admin view.
     */
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(Order::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new order manually (Admin utility).
     */
    @Transactional
    public OrderDTO createOrder(OrderDTO dto) {
        Order order = new Order();
        order.setTotalPrice(dto.getTotalPrice());
        order.setSecretCode(dto.getSecretCode() != null ? dto.getSecretCode() : generateSecretCode());

        linkOrderEntities(order, dto);

        return orderRepository.save(order).toDTO();
    }

    /**
     * Updates an existing order.
     */
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        order.setTotalPrice(dto.getTotalPrice());
        order.setSecretCode(dto.getSecretCode());
        
        linkOrderEntities(order, dto);

        return orderRepository.save(order).toDTO();
    }

    /**
     * Deletes an order by ID.
     */
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

    /**
     * Logic for Checkout (from main)
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
            .orElseThrow(() -> new ResourceNotFoundException("Order status not found: " + DEFAULT_ORDER_STATUS));

        Rider assignedRider = riderRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("No riders available"));

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
     * Rejects a specific order on behalf of a restaurant.
     * Business rules applied:
     * - Order must exist.
     * - Verifies the order belongs to the restaurant rejecting it.
     * - Changes order status to 'Cancelled'.
     * - Saves the detailed rejection reason (Requirement CO2).
     */
    @Transactional
    public OrderDTO rejectOrder(Long restaurantId, Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        if (order.getDishes().isEmpty() || !order.getDishes().get(0).getRestaurant().getId().equals(restaurantId)) {
            throw new IllegalArgumentException("This order does not belong to your restaurant");
        }

        OrderStatus cancelledStatus = orderStatusRepository.findByStatusIgnoreCase("Cancelled")
                .orElseThrow(() -> new ResourceNotFoundException("Cancelled status not found"));

        order.setStatus(cancelledStatus);
        order.setRejectionReason(reason);

        Order updatedOrder = orderRepository.save(order);
        return updatedOrder.toDTO();
    }

    // --- Private Helper Methods ---

    private void linkOrderEntities(Order order, OrderDTO dto) {
        if (dto.getCustomerId() != null) {
            customerRepository.findById(dto.getCustomerId()).ifPresent(order::setCustomer);
        }
        if (dto.getRiderId() != null) {
            riderRepository.findById(dto.getRiderId()).ifPresent(order::setRider);
        }
        if (dto.getStatusId() != null) {
            orderStatusRepository.findById(dto.getStatusId()).ifPresent(order::setStatus);
        }
        if (dto.getDishIds() != null && !dto.getDishIds().isEmpty()) {
            order.setDishes(dishRepository.findAllById(dto.getDishIds()));
        }
    }

    /**
     * Validates mandatory checkout fields and basic constraints.
     *
     * @param request checkout request payload
     * @throws IllegalArgumentException when request is null, customerId is missing,
     * dishIds is empty/invalid, or clientTotal is negative
     */
    private void validateCheckoutRequest(CheckoutOrderRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Payload required");
        if (request.getCustomerId() == null) throw new IllegalArgumentException("customerId required");
        if (request.getDishIds() == null || request.getDishIds().isEmpty()) throw new IllegalArgumentException("dishIds required");
        if (request.getClientTotal() < 0) throw new IllegalArgumentException("Total cannot be negative");
    }

    private void validatePayment(String paymentToken, double clientTotal, double calculatedTotal) {
        if (paymentToken == null || paymentToken.isBlank()) throw new IllegalArgumentException("Invalid payment token");
        if (Math.abs(clientTotal - calculatedTotal) > 0.01) throw new IllegalArgumentException("Total mismatch");
    }

    private double calculateTotal(List<Dish> dishes) {
        return dishes.stream().mapToDouble(Dish::getPrice).sum();
    }

    private List<Dish> resolveRequestedDishes(List<Long> dishIds) {
        return dishIds.stream()
            .map(id -> dishRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Dish not found: " + id)))
            .collect(Collectors.toList());
    }

    private String generateSecretCode() {
        return String.valueOf(RANDOM.nextInt(900000) + 100000);
    }
}