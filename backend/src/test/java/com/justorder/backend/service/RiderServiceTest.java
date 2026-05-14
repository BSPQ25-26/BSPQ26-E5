package com.justorder.backend.service;

import com.justorder.backend.dto.OrderDTO;
import com.justorder.backend.model.Customer;
import com.justorder.backend.model.Localization;
import com.justorder.backend.model.Order;
import com.justorder.backend.model.OrderStatus;
import com.justorder.backend.model.Rider;
import com.justorder.backend.repository.OrderRepository;
import com.justorder.backend.repository.OrderStatusRepository;
import com.justorder.backend.repository.RiderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RiderService}.
 *
 * <p>All external dependencies (repositories, security service) are mocked so
 * tests run without a Spring context or database.</p>
 */
@ExtendWith(MockitoExtension.class)
class RiderServiceTest {

    // -------------------------------------------------------------------------
    // Mocks
    // -------------------------------------------------------------------------

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RiderRepository riderRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @Mock
    private OrderPinSecurityService orderPinSecurityService;

    @InjectMocks
    private RiderService riderService;

    // -------------------------------------------------------------------------
    // Test fixtures
    // -------------------------------------------------------------------------

    private Rider rider1;
    private Rider rider2;
    private Customer customer;
    private OrderStatus pendingStatus;
    private OrderStatus inTransitStatus;
    private OrderStatus deliveredStatus;
    private OrderStatus cancelledStatus;
    private Order unassignedOrder;
    private Order assignedOrder;

    @BeforeEach
    void setUp() {
        Localization loc = new Localization("Madrid", "Madrid", "Spain", "28001", "1", 40.4, -3.7);

        rider1 = new Rider("Rider One", "11111111A", "+34611111111", "rider1@test.com", "pass", loc);
        rider1.setId(1L);

        rider2 = new Rider("Rider Two", "22222222B", "+34622222222", "rider2@test.com", "pass", loc);
        rider2.setId(2L);

        customer = new Customer("Customer", "cust@test.com", "+34600000000", "pass", 25,
                "33333333C", List.of(loc), List.of(), List.of());
        customer.setId(10L);

        pendingStatus   = new OrderStatus("Pending");
        inTransitStatus = new OrderStatus("In Transit");
        deliveredStatus = new OrderStatus("Delivered");
        cancelledStatus = new OrderStatus("Cancelled");

        // An order without a rider assigned yet
        unassignedOrder = new Order(customer, List.of(), pendingStatus, null, 20.0, "hashedPin");
        unassignedOrder.setId(100L);

        // An order already assigned to rider1
        assignedOrder = new Order(customer, List.of(), pendingStatus, rider1, 30.0, "hashedPin");
        assignedOrder.setId(200L);
    }

    // =========================================================================
    // assignOrder — rider claims an available order
    // =========================================================================

    /**
     * A rider successfully claims an unassigned order.
     * Verifies the returned DTO contains the rider's id and the order is saved.
     */
    @Test
    void assignOrder_whenOrderIsUnassigned_shouldAssignToRider() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(unassignedOrder));
        when(riderRepository.findById(1L)).thenReturn(Optional.of(rider1));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = riderService.assignOrder(1L, 100L, new OrderDTO());

        assertThat(result.getRiderId()).isEqualTo(1L);
        verify(orderRepository).save(unassignedOrder);
    }

    /**
     * A second rider tries to claim an order already assigned to rider1.
     * The service must throw {@link IllegalStateException} so no double-claim occurs.
     */
    @Test
    void assignOrder_whenOrderAlreadyAssigned_shouldThrowIllegalStateException() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));

        assertThrows(
                IllegalStateException.class,
                () -> riderService.assignOrder(2L, 200L, new OrderDTO()),
                "Order is already assigned to a rider."
        );

        verify(orderRepository, never()).save(any());
    }

    /**
     * Attempting to assign a non-existent order throws {@link IllegalArgumentException}.
     */
    @Test
    void assignOrder_whenOrderNotFound_shouldThrowIllegalArgumentException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> riderService.assignOrder(1L, 999L, new OrderDTO())
        );
    }

    /**
     * Attempting to assign an order for a non-existent rider throws {@link IllegalArgumentException}.
     */
    @Test
    void assignOrder_whenRiderNotFound_shouldThrowIllegalArgumentException() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(unassignedOrder));
        when(riderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> riderService.assignOrder(99L, 100L, new OrderDTO())
        );
    }

    // =========================================================================
    // updateOrderStatus — advancing delivery states
    // =========================================================================

    /**
     * Rider advances the order status to "In Transit".
     * Verifies the new status is persisted and returned in the DTO.
     */
    @Test
    void updateOrderStatus_whenValidTransition_shouldPersistNewStatus() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));
        when(orderStatusRepository.findByStatusIgnoreCase("In Transit"))
                .thenReturn(Optional.of(inTransitStatus));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = riderService.updateOrderStatus(1L, 200L, "In Transit");

        assertThat(result.getStatus()).isEqualTo("In Transit");
        verify(orderRepository).save(assignedOrder);
    }

    /**
     * A rider who is NOT assigned to an order cannot update its status → {@link SecurityException}.
     */
    @Test
    void updateOrderStatus_whenWrongRider_shouldThrowSecurityException() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));

        // rider2 tries to update an order assigned to rider1
        assertThrows(
                SecurityException.class,
                () -> riderService.updateOrderStatus(2L, 200L, "In Transit")
        );

        verify(orderStatusRepository, never()).findByStatusIgnoreCase(any());
    }

    /**
     * Trying to set status directly to "Delivered" via {@code updateOrderStatus}
     * must throw {@link IllegalArgumentException} — PIN verification is required.
     */
    @Test
    void updateOrderStatus_toDelivered_shouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> riderService.updateOrderStatus(1L, 200L, "Delivered"),
                "Use PIN verification to set status to Delivered."
        );

        verify(orderRepository, never()).findById(any());
    }

    /**
     * Requesting an unknown status string throws {@link IllegalArgumentException}.
     */
    @Test
    void updateOrderStatus_whenStatusNotFound_shouldThrowIllegalArgumentException() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));
        when(orderStatusRepository.findByStatusIgnoreCase("Teleported"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> riderService.updateOrderStatus(1L, 200L, "Teleported")
        );
    }

    // =========================================================================
    // verifyOrderPin — secret PIN delivery confirmation
    // =========================================================================

    /**
     * Rider submits the correct 6-digit PIN.
     * Order must be marked as "Delivered" with {@code deliveredAt} and {@code pinVerifiedAt} set.
     */
    @Test
    void verifyOrderPin_withCorrectPin_shouldMarkOrderAsDelivered() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));
        when(orderPinSecurityService.matches("123456", "hashedPin")).thenReturn(true);
        when(orderStatusRepository.findByStatusIgnoreCase("Delivered"))
                .thenReturn(Optional.of(deliveredStatus));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = riderService.verifyOrderPin(1L, 200L, "123456");

        assertThat(result.getStatus()).isEqualTo("Delivered");
        verify(orderRepository).save(assignedOrder);
    }

    /**
     * Wrong PIN does NOT mark the order as delivered and throws {@link IllegalArgumentException}.
     */
    @Test
    void verifyOrderPin_withWrongPin_shouldThrowAndIncrementFailedAttempts() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));
        when(orderPinSecurityService.matches("000000", "hashedPin")).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> riderService.verifyOrderPin(1L, 200L, "000000")
        );

        assertThat(assignedOrder.getPinFailedAttempts()).isEqualTo(1);
        verify(orderStatusRepository, never()).findByStatusIgnoreCase(any());
    }

    /**
     * After 5 consecutive wrong PINs the account is temporarily locked and subsequent
     * attempts throw {@link IllegalStateException}.
     */
    @Test
    void verifyOrderPin_afterFiveWrongAttempts_shouldLockPinVerification() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));
        when(orderPinSecurityService.matches(anyString(), eq("hashedPin"))).thenReturn(false);

        // Exhaust 5 attempts (each save is a no-op in the mock)
        for (int i = 0; i < 5; i++) {
            try {
                riderService.verifyOrderPin(1L, 200L, "000000");
            } catch (IllegalArgumentException ignored) { /* expected */ }
        }

        // After 5 failures the order gets a lock timestamp
        assertThat(assignedOrder.getPinLockedUntil()).isNotNull();
        assertThat(assignedOrder.getPinLockedUntil()).isAfter(LocalDateTime.now());
    }

    /**
     * If PIN verification is locked, a further attempt throws {@link IllegalStateException}
     * immediately without even checking the PIN.
     */
    @Test
    void verifyOrderPin_whenLocked_shouldThrowIllegalStateException() {
        assignedOrder.setPinLockedUntil(LocalDateTime.now().plusMinutes(5));
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));

        assertThrows(
                IllegalStateException.class,
                () -> riderService.verifyOrderPin(1L, 200L, "123456")
        );

        verify(orderPinSecurityService, never()).matches(any(), any());
    }

    /**
     * If the order is already delivered, verifyOrderPin is idempotent and returns
     * the current DTO without throwing or re-saving.
     */
    @Test
    void verifyOrderPin_whenAlreadyDelivered_shouldReturnCurrentDtoIdempotently() {
        assignedOrder.setStatus(deliveredStatus);
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));

        OrderDTO result = riderService.verifyOrderPin(1L, 200L, "123456");

        assertThat(result.getStatus()).isEqualTo("Delivered");
        verify(orderPinSecurityService, never()).matches(any(), any());
        verify(orderRepository, never()).save(any());
    }

    /**
     * A non-6-digit PIN string fails format validation immediately with
     * {@link IllegalArgumentException}.
     */
    @Test
    void verifyOrderPin_withMalformedPin_shouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> riderService.verifyOrderPin(1L, 200L, "abc")
        );

        verify(orderRepository, never()).findById(any());
    }

    /**
     * A rider who is NOT assigned to the order cannot verify the PIN → {@link SecurityException}.
     */
    @Test
    void verifyOrderPin_withWrongRider_shouldThrowSecurityException() {
        when(orderRepository.findById(200L)).thenReturn(Optional.of(assignedOrder));

        // rider2 is not assigned to this order
        assertThrows(
                SecurityException.class,
                () -> riderService.verifyOrderPin(2L, 200L, "123456")
        );

        verify(orderPinSecurityService, never()).matches(any(), any());
    }

    // =========================================================================
    // getAvailableOrders — only returns unassigned pending orders
    // =========================================================================

    /**
     * Only orders without a rider AND with "Pending" status appear in the available list.
     */
    @Test
    void getAvailableOrders_shouldOnlyReturnUnassignedPendingOrders() {
        // unassignedOrder: no rider, Pending  ← should be included
        // assignedOrder: has rider, Pending   ← should NOT be included (already has rider)
        when(orderRepository.findByRiderIsNull()).thenReturn(List.of(unassignedOrder));

        List<OrderDTO> result = riderService.getAvailableOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRiderId()).isNull();
    }
}
