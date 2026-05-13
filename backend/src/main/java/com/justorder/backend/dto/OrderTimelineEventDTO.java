package com.justorder.backend.dto;

import java.time.LocalDateTime;

public class OrderTimelineEventDTO {

    private Long id;
    private Long orderId;
    private String statusOrEvent;
    private String details;
    private LocalDateTime timestamp;

    public OrderTimelineEventDTO() {}

    public OrderTimelineEventDTO(Long id, Long orderId, String statusOrEvent, String details, LocalDateTime timestamp) {
        this.id = id;
        this.orderId = orderId;
        this.statusOrEvent = statusOrEvent;
        this.details = details;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getStatusOrEvent() { return statusOrEvent; }
    public void setStatusOrEvent(String statusOrEvent) { this.statusOrEvent = statusOrEvent; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
