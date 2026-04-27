package com.justorder.backend.service;

import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.dto.RiderDashboardDTO;
import com.justorder.backend.dto.RiderDTO;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RiderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RiderService {

    private static final int MAX_PIN_ATTEMPTS = 5;
    private static final int PIN_LOCK_MINUTES = 10;

    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderPinSecurityService orderPinSecurityService;


    public RiderService(OrderRepository orderRepository,
                        RiderRepository riderRepository,
                        OrderStatusRepository orderStatusRepository,
                        OrderPinSecurityService orderPinSecurityService) {
        this.orderRepository = orderRepository;
        this.riderRepository = riderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderPinSecurityService = orderPinSecurityService;
    }

        @Transactional(readOnly = true)
        public RiderDTO getRider(Long riderId) {
        Rider rider = riderRepository.findById(riderId)
                    .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));
        return rider.toDTO();
        }

        @Transactional(readOnly = true)
        public RiderDashboardDTO getRiderDashboard(Long riderId) {
        Rider rider = riderRepository.findById(riderId)
                    .orElseThrow(() -> new IllegalArgumentException("Rider not found with id: " + riderId));

        List<Order> riderOrders = orderRepository.findByRiderId(riderId);

        long pendingOrders = riderOrders.stream()
                    .filter(order -> isStatus(order, "Pending"))
                    .count();
        long deliveredOrders = riderOrders.stream()
                    .filter(order -> isStatus(order, "Delivered"))
                    .count();
        long cancelledOrders = riderOrders.stream()
                    .filter(order -> isStatus(order, "Cancelled"))
                    .count();
        long inProgressOrders = riderOrders.size() - pendingOrders - deliveredOrders - cancelledOrders;

        List<OrderDTO> assignedOrders = riderOrders.stream()
                    .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .map(Order::toDTO)
                    .toList();

        return new RiderDashboardDTO(
                    riderId,
                    rider.getName(),
                    riderOrders.size(),
                    pendingOrders,
                    inProgressOrders,
                    deliveredOrders,
                    cancelledOrders,
                    assignedOrders
            );
        }

        private boolean isStatus(Order order, String expectedStatus) {
        return order.getStatus() != null
                    && expectedStatus.equalsIgnoreCase(order.getStatus().getStatus());
        }


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


    @Transactional
    public OrderDTO verifyOrderPin(Long riderId, Long orderId, String pin) {
        if (pin == null || !pin.matches("\\d{6}")) {
            throw new IllegalArgumentException("PIN must be a 6-digit numeric code");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with id: " + orderId));

        if (!order.getRider().getId().equals(riderId)) {
            throw new SecurityException(
                    "Rider " + riderId + " is not assigned to order " + orderId);
        }

        if ("Delivered".equalsIgnoreCase(order.getStatus().getStatus())) {
            return order.toDTO();
        }

        LocalDateTime now = LocalDateTime.now();
        if (order.getPinLockedUntil() != null && order.getPinLockedUntil().isAfter(now)) {
            throw new IllegalStateException("PIN verification is temporarily locked");
        }

        boolean pinMatches = orderPinSecurityService.matches(pin, order.getSecretCodeHash());
        if (!pinMatches) {
            int failedAttempts = order.getPinFailedAttempts() + 1;
            order.setPinFailedAttempts(failedAttempts);

            if (failedAttempts >= MAX_PIN_ATTEMPTS) {
                order.setPinLockedUntil(now.plusMinutes(PIN_LOCK_MINUTES));
                order.setPinFailedAttempts(0);
            }

            orderRepository.save(order);
            throw new IllegalArgumentException("Invalid verification PIN");
        }

        OrderStatus deliveredStatus = orderStatusRepository.findByStatusIgnoreCase("Delivered")
                .orElseThrow(() -> new IllegalArgumentException("Delivered status not found"));

        order.setStatus(deliveredStatus);
        order.setDeliveredAt(now);
        order.setPinVerifiedAt(now);
        order.setPinFailedAttempts(0);
        order.setPinLockedUntil(null);

        return orderRepository.save(order).toDTO();
    }
}