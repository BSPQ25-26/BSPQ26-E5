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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}