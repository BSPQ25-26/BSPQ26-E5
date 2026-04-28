package com.justorder.backend.dto;

import java.util.List;

public class RestaurantDashboardDTO {

    private Long restaurantId;
    private long totalOrders;
    private long activeOrders;
    private long cancelledOrders;
    private long deliveredOrders;
    private double totalRevenue;
    private double totalRefunded;
    private List<OrderDTO> recentOrders;

    public RestaurantDashboardDTO() {
    }

    public RestaurantDashboardDTO(Long restaurantId,
                                  long totalOrders,
                                  long activeOrders,
                                  long cancelledOrders,
                                  long deliveredOrders,
                                  double totalRevenue,
                                  double totalRefunded,
                                  List<OrderDTO> recentOrders) {
        this.restaurantId = restaurantId;
        this.totalOrders = totalOrders;
        this.activeOrders = activeOrders;
        this.cancelledOrders = cancelledOrders;
        this.deliveredOrders = deliveredOrders;
        this.totalRevenue = totalRevenue;
        this.totalRefunded = totalRefunded;
        this.recentOrders = recentOrders;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
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

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
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
