package com.justorder.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;

import com.justorder.backend.dto.OrderDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
     */
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatus status;

    /**
     * The rider assigned to deliver this order.
     */
    @ManyToOne
    @JoinColumn(name = "rider_id", nullable = false)
    private Rider rider;

    /** Pre-calculated total price of all dishes in the order, in euros. */
    private double totalPrice;

    /**
     * The rider must ask for this code upon delivery to verify the recipient.
     */
    private String secretCode;

    /**
     * Null if the order has not been rejected by a rider.
     */
    private String rejectionReason;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    // --- Constructors ---

    public Order() {}

    public Order(Customer customer, List<Dish> dishes, OrderStatus status, Rider rider,
                 double totalPrice, String secretCode) {
        this.customer = customer;
        this.dishes = dishes;
        this.status = status;
        this.rider = rider;
        this.totalPrice = totalPrice;
        this.secretCode = secretCode;
    }

    // --- Getters ---

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Dish> getDishes() { return dishes; }
    public OrderStatus getStatus() { return status; }
    public Rider getRider() { return rider; }
    public double getTotalPrice() { return totalPrice; }
    public String getSecretCode() { return secretCode; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    // --- Setters ---

    public void setId(Long id) { this.id = id; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setDishes(List<Dish> dishes) { this.dishes = dishes; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setRider(Rider rider) { this.rider = rider; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    // --- Conversion ---

    public OrderDTO toDTO() {
        // We use IDs in the DTO to avoid recursion and reduce payload size
        OrderDTO dto = new OrderDTO(
            this.id, 
            this.customer != null ? this.customer.getId() : null, 
            this.status != null ? this.status.getStatus() : null,
            this.rider != null ? this.rider.getId() : null, 
            this.totalPrice, 
            this.secretCode
        );
        dto.setRejectionReason(this.rejectionReason);
        if (this.dishes != null) {
            dto.setDishes(this.dishes.stream().map(Dish::toDTO).collect(Collectors.toList()));
        }
        dto.setCreatedAt(this.createdAt);
        dto.setDeliveredAt(this.deliveredAt);
        return dto;
    }
}