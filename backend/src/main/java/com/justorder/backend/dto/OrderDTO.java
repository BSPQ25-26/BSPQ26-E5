package com.justorder.backend.dto;

import java.util.List;

public class OrderDTO {

    private Long id;
    private Long customerId;
    private Long riderId;
    private Long statusId;
    private List<Long> dishIds;
    private double totalPrice;
    private String secretCode;

    public OrderDTO() {
    }

    // Getters
    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Long getRiderId() { return riderId; }
    public Long getStatusId() { return statusId; }
    public List<Long> getDishIds() { return dishIds; }
    public double getTotalPrice() { return totalPrice; }
    public String getSecretCode() { return secretCode; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setRiderId(Long riderId) { this.riderId = riderId; }
    public void setStatusId(Long statusId) { this.statusId = statusId; }
    public void setDishIds(List<Long> dishIds) { this.dishIds = dishIds; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }
}