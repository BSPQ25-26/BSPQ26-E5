package com.justorder.backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final DishRepository dishRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final RiderRepository riderRepository;
    private final OrderPinGenerationService orderPinGenerationService;
    private final OrderPinSecurityService orderPinSecurityService;

    public OrderService(
        OrderRepository orderRepository,
        CustomerRepository customerRepository,
        DishRepository dishRepository,
        OrderStatusRepository orderStatusRepository,
        RiderRepository riderRepository,
        OrderPinGenerationService orderPinGenerationService,
        OrderPinSecurityService orderPinSecurityService
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.dishRepository = dishRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.riderRepository = riderRepository;
        this.orderPinGenerationService = orderPinGenerationService;
        this.orderPinSecurityService = orderPinSecurityService;
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
        
        // Si el DTO no trae PIN, generamos uno nuevo y lo encriptamos
        if (dto.getSecretCode() != null) {
             order.setSecretCodeHash(orderPinSecurityService.hashPin(dto.getSecretCode()));
        } else {
             order.setSecretCodeHash(orderPinSecurityService.hashPin(orderPinGenerationService.generatePin()));
        }

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
        
        if (dto.getSecretCode() != null && !dto.getSecretCode().isBlank()) {
            order.setSecretCodeHash(orderPinSecurityService.hashPin(dto.getSecretCode()));
        }
        
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
     * Logic for Checkout
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

        // Refactor: Efficiently find a single rider from the DB instead of loading ALL of them.
        Page<Rider> riderPage = riderRepository.findAll(PageRequest.of(0, 1));
        if (!riderPage.hasContent()) {
             throw new ResourceNotFoundException("No riders available");
        }
        Rider assignedRider = riderPage.getContent().get(0);

        Order order = new Order();
        order.setCustomer(customer);
        order.setDishes(dishes);
        order.setStatus(status);
        order.setRider(assignedRider);
        order.setTotalPrice(calculatedTotal);
        String plainPin = orderPinGenerationService.generatePin();
        order.setSecretCodeHash(orderPinSecurityService.hashPin(plainPin));

        OrderDTO createdOrder = orderRepository.save(order).toDTO();
        createdOrder.setSecretCode(plainPin);
        return createdOrder;
    }

    /**
     * Rejects a specific order on behalf of a restaurant.
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
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + dto.getCustomerId()));
            order.setCustomer(customer);
        }
        if (dto.getRiderId() != null) {
            Rider rider = riderRepository.findById(dto.getRiderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rider not found: " + dto.getRiderId()));
            order.setRider(rider);
        }
        if (dto.getStatusId() != null) {
            OrderStatus status = orderStatusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order status not found: " + dto.getStatusId()));
            order.setStatus(status);
        }
        if (dto.getDishIds() != null && !dto.getDishIds().isEmpty()) {
            List<Dish> dishes = dishRepository.findAllById(dto.getDishIds());
            if (dishes.size() != dto.getDishIds().size()) {
                throw new ResourceNotFoundException("One or more dishes from the provided IDs were not found");
            }
            order.setDishes(dishes);
        }
    }

    /**
     * Validates mandatory checkout fields and basic constraints.
     */
    private void validateCheckoutRequest(CheckoutOrderRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Payload required");
        if (request.getCustomerId() == null) throw new IllegalArgumentException("customerId required");
        if (request.getDishIds() == null || request.getDishIds().isEmpty()) {
            throw new IllegalArgumentException("dishIds required");
        }
        
        // Ensure no null or invalid IDs were passed inside the array
        for (Long dishId : request.getDishIds()) {
            if (dishId == null || dishId <= 0) {
                throw new IllegalArgumentException("Invalid dish ID: must be non-null and greater than 0");
            }
        }
        
        if (request.getClientTotal() < 0) throw new IllegalArgumentException("Total cannot be negative");
    }

    private void validatePayment(String paymentToken, double clientTotal, double calculatedTotal) {
        if (paymentToken == null || paymentToken.isBlank()) throw new IllegalArgumentException("Invalid payment token");
        if (Math.abs(clientTotal - calculatedTotal) > 0.01) throw new IllegalArgumentException("Total mismatch");
    }

    private double calculateTotal(List<Dish> dishes) {
        return dishes.stream().mapToDouble(Dish::getPrice).sum();
    }

    /**
     * Resolves requested dishes efficiently using a single database query.
     */
    private List<Dish> resolveRequestedDishes(List<Long> dishIds) {
        // Fetch all unique dishes in a single query to avoid the N+1 problem
        List<Long> uniqueDishIds = dishIds.stream().distinct().collect(Collectors.toList());
        List<Dish> fetchedDishes = dishRepository.findAllById(uniqueDishIds);

        // Map them for O(1) fast lookup
        Map<Long, Dish> dishMap = fetchedDishes.stream()
                .collect(Collectors.toMap(Dish::getId, dish -> dish));

        // Reconstruct the requested list (preserves duplicates if someone orders 2 of the same dish)
        return dishIds.stream().map(id -> {
            Dish dish = dishMap.get(id);
            if (dish == null) {
                throw new ResourceNotFoundException("Dish not found: " + id);
            }
            return dish;
        }).collect(Collectors.toList());
    }

}