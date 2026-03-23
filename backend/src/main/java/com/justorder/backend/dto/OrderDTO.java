package com.justorder.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private Long id;
    private Long customerId;
    private Long riderId;
    private Long statusId;     // Para crear/actualizar (ID numérico)
    private String status;     // Para mostrar al usuario (ej: "Pending")
    private List<Long> dishIds;    // Para crear pedidos desde el front
    private List<DishDTO> dishes;  // Para enviar el detalle de los platos al front
    private double totalPrice;
    private String secretCode;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    public OrderDTO() {
    }

    // Constructor necesario para Order.toDTO()
    public OrderDTO(Long id, Long customerId, String status, Long riderId, double totalPrice, String secretCode) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.riderId = riderId;
        this.totalPrice = totalPrice;
        this.secretCode = secretCode;
    }

    // --- Getters ---
    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Long getRiderId() { return riderId; }
    public Long getStatusId() { return statusId; }
    public String getStatus() { return status; }
    public List<Long> getDishIds() { return dishIds; }
    public List<DishDTO> getDishes() { return dishes; }
    public double getTotalPrice() { return totalPrice; }
    public String getSecretCode() { return secretCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public String getRejectionReason() { return rejectionReason; }

    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }
    public void setStatusId(Long statusId) { this.statusId = statusId; }
    public void setStatus(String status) { this.status = status; }
    public void setDishIds(List<Long> dishIds) { this.dishIds = dishIds; }
    public void setDishes(List<DishDTO> dishes) { this.dishes = dishes; } // Corregido a setDishes
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
}
