package com.justorder.backend.dto;

public class OrderStatusDTO {

    private Long id;
    private String status;

    public OrderStatusDTO() {
    }

    public OrderStatusDTO(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    /**
     * Constructor for compatibility with main branch logic.
     */
    public OrderStatusDTO(String status) {
        this.status = status;
    }

    // Getters
    public Long getId() { return id; }
    public String getStatus() { return status; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setStatus(String status) { this.status = status; }
}