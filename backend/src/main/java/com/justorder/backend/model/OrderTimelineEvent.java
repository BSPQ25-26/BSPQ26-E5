package com.justorder.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import com.justorder.backend.dto.OrderTimelineEventDTO;

@Entity
@Table(name = "order_timeline_events")
public class OrderTimelineEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String statusOrEvent;

    @Column(length = 500)
    private String details;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public OrderTimelineEvent() {
    }

    public OrderTimelineEvent(Order order, String statusOrEvent, String details) {
        this.order = order;
        this.statusOrEvent = statusOrEvent;
        this.details = details;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getStatusOrEvent() { return statusOrEvent; }
    public void setStatusOrEvent(String statusOrEvent) { this.statusOrEvent = statusOrEvent; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public OrderTimelineEventDTO toDTO() {
        return new OrderTimelineEventDTO(
            this.id,
            this.order.getId(),
            this.statusOrEvent,
            this.details,
            this.timestamp
        );
    }
}
