package com.justorder.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;

import com.justorder.backend.dto.OrderDTO;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToMany
    @JoinTable(
        name = "order_dishes",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes = new ArrayList<>();

    /**
     * The current status of the order (e.g. Pending, Confirmed, Cancelled).
     * Updated throughout the order lifecycle by restaurant, rider, and system.
     */
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatus status;

    /**
     * The rider assigned to deliver this order.
     */
    @ManyToOne
    @JoinColumn(name = "rider_id", nullable = true)
    private Rider rider;

    /** Pre-calculated total price of all dishes in the order, in euros. */
    private double totalPrice;

    /** BCrypt hash of the delivery verification PIN. */
    @Column(name = "secret_code", nullable = false)
    private String secretCodeHash;

    /** Failed verification attempts by the assigned rider. */
    private int pinFailedAttempts;

    /** When present and in the future, PIN verification is temporarily locked. */
    private LocalDateTime pinLockedUntil;

    /** Timestamp when rider successfully verified the PIN and marked delivery complete. */
    private LocalDateTime pinVerifiedAt;

    /**
     * <p>Null if the order has not been rejected by a rider.</p>
     */
    private String rejectionReason;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @jakarta.persistence.OrderBy("timestamp ASC")
    private List<OrderTimelineEvent> timelineEvents = new ArrayList<>();

    public Order() {
    }

    public void addTimelineEvent(String statusOrEvent, String details) {
        OrderTimelineEvent event = new OrderTimelineEvent(this, statusOrEvent, details);
        this.timelineEvents.add(event);
    }


    public Order(Customer customer, List<Dish> dishes, OrderStatus status, Rider rider,
                 double totalPrice, String secretCodeHash) {
        this.customer = customer;
        this.dishes = dishes;
        this.status = status;
        this.rider = rider;
        this.totalPrice = totalPrice;
        this.secretCodeHash = secretCodeHash;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Dish> getDishes() { return dishes; }
    public OrderStatus getStatus() { return status; }
    public Rider getRider() { return rider; }
    public double getTotalPrice() { return totalPrice; }
    public String getSecretCodeHash() { return secretCodeHash; }
    public int getPinFailedAttempts() { return pinFailedAttempts; }
    public LocalDateTime getPinLockedUntil() { return pinLockedUntil; }
    public LocalDateTime getPinVerifiedAt() { return pinVerifiedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public List<OrderTimelineEvent> getTimelineEvents() { return timelineEvents; }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setId(Long id) { this.id = id; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setDishes(List<Dish> dishes) { this.dishes = dishes; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setRider(Rider rider) { this.rider = rider; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setSecretCodeHash(String secretCodeHash) { this.secretCodeHash = secretCodeHash; }
    public void setPinFailedAttempts(int pinFailedAttempts) { this.pinFailedAttempts = pinFailedAttempts; }
    public void setPinLockedUntil(LocalDateTime pinLockedUntil) { this.pinLockedUntil = pinLockedUntil; }
    public void setPinVerifiedAt(LocalDateTime pinVerifiedAt) { this.pinVerifiedAt = pinVerifiedAt; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public void setTimelineEvents(List<OrderTimelineEvent> timelineEvents) { this.timelineEvents = timelineEvents; }

    // -------------------------------------------------------------------------
    // Conversion
    // -------------------------------------------------------------------------

    public OrderDTO toDTO() {
        OrderDTO dto = new OrderDTO(
            this.id, this.customer.getId(), this.status.getStatus(),
            this.rider != null ? this.rider.getId() : null, this.totalPrice, null
        );
        dto.setRejectionReason(this.rejectionReason);
        dto.setdishes(this.dishes.stream().map(Dish::toDTO).collect(Collectors.toList()));
        dto.setCreatedAt(this.createdAt);
        dto.setDeliveredAt(this.deliveredAt);
        dto.setRejectionReason(this.rejectionReason);
        if (this.timelineEvents != null) {
            dto.setTimeline(this.timelineEvents.stream().map(OrderTimelineEvent::toDTO).collect(Collectors.toList()));
        }
        return dto;
    }
}