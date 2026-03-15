package com.justorder.backend.service;

import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RiderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RiderService {

    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final OrderStatusRepository orderStatusRepository;

    /**
     * Constructor injection of all required repositories.
     *
     * @param orderRepository       For loading and saving orders.
     * @param riderRepository       For finding available riders to reassign to.
     * @param orderStatusRepository For looking up the "Cancelled" status by name.
     */
    public RiderService(OrderRepository orderRepository,
                        RiderRepository riderRepository,
                        OrderStatusRepository orderStatusRepository) {
        this.orderRepository = orderRepository;
        this.riderRepository = riderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    /**
     * Processes a rider's rejection of an assigned order (CO3).
     *
     * @param riderId         ID of the rider attempting to reject the order.
     * @param orderId         ID of the order to reject.
     * @param rejectionReason Human-readable reason why delivery is not possible.
     * @return The updated order as a DTO.
     * @throws IllegalArgumentException if the order or "Cancelled" status is not found.
     * @throws SecurityException        if the order does not belong to this rider.
     */
    @Transactional
    public OrderDTO rejectOrder(Long riderId, Long orderId, String rejectionReason) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with id: " + orderId));

        if (!order.getRider().getId().equals(riderId)) {
            throw new SecurityException(
                    "Rider " + riderId + " is not assigned to order " + orderId);
        }

        boolean alreadyRejectedOnce = order.getRejectionReason() != null;

        order.setRejectionReason(rejectionReason);

        Optional<Rider> otherRider = alreadyRejectedOnce
                ? Optional.empty()
                : riderRepository.findAll()
                        .stream()
                        .filter(r -> !r.getId().equals(riderId))
                        .findFirst();

        if (otherRider.isPresent()) {
            order.setRider(otherRider.get());
        } else {
            OrderStatus cancelledStatus = orderStatusRepository
                    .findByStatus("Cancelled")
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Order status 'Cancelled' not found. Check DataInitializer."));
            order.setStatus(cancelledStatus);
        }

        Order savedOrder = orderRepository.save(order);
        return savedOrder.toDTO();
    }

    /**
     * Returns all orders assigned to a specific rider.
     *
     * <p>Used by {@code GET /api/riders/{riderId}/orders} to let a rider
     * view their assigned deliveries.</p>
     *
     * @param riderId The ID of the rider whose orders to retrieve.
     * @return List of the rider's orders as DTOs. Empty list if none assigned.
     * @throws IllegalArgumentException if no rider exists with the given ID.
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getRiderOrders(Long riderId) {
        if (!riderRepository.existsById(riderId)) {
            throw new IllegalArgumentException("Rider not found with id: " + riderId);
        }
        return orderRepository.findByRiderId(riderId)
                .stream()
                .map(Order::toDTO)
                .toList();
    }
}