package com.justorder.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used to expose order data through the API.
 */
public class OrderDTO {

    private Long id;
    private Long customerId;
    private List<DishDTO> dishes;
    private String status;
    private Long riderId;
    private double totalPrice;
    private String secretCode;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    public OrderDTO() {
    }

    /**
     * Creates a DTO with the core fields returned after checkout or status reads.
     *
     * @param id order id
     * @param customerId customer id
     * @param status current order status
     * @param riderId assigned rider id
     * @param totalPrice final order total
     * @param secretCode delivery verification code
     */
    public OrderDTO(Long id, Long customerId, String status, Long riderId, double totalPrice, String secretCode) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.riderId = riderId;
        this.totalPrice = totalPrice;
        this.secretCode = secretCode;
    }

    // Getters
    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public List<DishDTO> getdishes() { return dishes; }
    public String getStatus() { return status; }
    public Long getRiderId() { return riderId; }
    public double getTotalPrice() { return totalPrice; }
    public String getSecretCode() { return secretCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setdishes(List<DishDTO> dishes) { this.dishes = dishes; }
    public void setStatus(String status) { this.status = status; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
}
