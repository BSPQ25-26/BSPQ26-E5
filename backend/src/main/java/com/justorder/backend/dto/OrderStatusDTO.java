package com.justorder.backend.dto;

public class OrderStatusDTO {

    private String status;

    public OrderStatusDTO() {
    }

    public OrderStatusDTO(String status) {
        this.status = status;
    }

    // Getters
    public String getStatus() { return status; }

    // Setters
    public void setStatus(String status) { this.status = status; }
}
