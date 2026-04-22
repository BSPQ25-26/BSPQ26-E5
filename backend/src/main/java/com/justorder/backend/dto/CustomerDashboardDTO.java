package com.justorder.backend.dto;

import java.util.List;

public class CustomerDashboardDTO {

    private Long customerId;
    private long totalOrders;
    private long activeOrders;
    private long cancelledOrders;
    private long deliveredOrders;
    private double totalSpent;
    private double totalRefunded;
    private List<OrderDTO> recentOrders;

    public CustomerDashboardDTO() {
    }

    public CustomerDashboardDTO(Long customerId,
                                long totalOrders,
                                long activeOrders,
                                long cancelledOrders,
                                long deliveredOrders,
                                double totalSpent,
                                double totalRefunded,
                                List<OrderDTO> recentOrders) {
        this.customerId = customerId;
        this.totalOrders = totalOrders;
        this.activeOrders = activeOrders;
        this.cancelledOrders = cancelledOrders;
        this.deliveredOrders = deliveredOrders;
        this.totalSpent = totalSpent;
        this.totalRefunded = totalRefunded;
        this.recentOrders = recentOrders;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(long activeOrders) {
        this.activeOrders = activeOrders;
    }

    public long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public long getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setDeliveredOrders(long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getTotalRefunded() {
        return totalRefunded;
    }

    public void setTotalRefunded(double totalRefunded) {
        this.totalRefunded = totalRefunded;
    }

    public List<OrderDTO> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<OrderDTO> recentOrders) {
        this.recentOrders = recentOrders;
    }
}
