package com.justorder.backend.model;

import com.justorder.backend.dto.OrderStatusDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_statuses")
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String status;

    public OrderStatus() {
    }

    public OrderStatus(String status) {
        this.status = status;
    }

    // Getters
    public Long getId() { return id; }
    public String getStatus() { return status; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setStatus(String status) { this.status = status; }

    // toDTO
    public OrderStatusDTO toDTO() {
        return new OrderStatusDTO(this.status);
    }
}
