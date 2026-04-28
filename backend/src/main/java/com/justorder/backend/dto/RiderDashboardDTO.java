package com.justorder.backend.dto;

import java.util.List;

public class RiderDashboardDTO {

    private Long riderId;
    private String riderName;
    private long totalOrders;
    private long pendingOrders;
    private long inProgressOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private List<OrderDTO> assignedOrders;

    public RiderDashboardDTO() {
    }

    public RiderDashboardDTO(Long riderId,
                             String riderName,
                             long totalOrders,
                             long pendingOrders,
                             long inProgressOrders,
                             long deliveredOrders,
                             long cancelledOrders,
                             List<OrderDTO> assignedOrders) {
        this.riderId = riderId;
        this.riderName = riderName;
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.inProgressOrders = inProgressOrders;
        this.deliveredOrders = deliveredOrders;
        this.cancelledOrders = cancelledOrders;
        this.assignedOrders = assignedOrders;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public long getInProgressOrders() {
        return inProgressOrders;
    }

    public void setInProgressOrders(long inProgressOrders) {
        this.inProgressOrders = inProgressOrders;
    }

    public long getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setDeliveredOrders(long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }

    public long getCancelledOrders() {
        return cancelledOrders;
    }

    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }

    public List<OrderDTO> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<OrderDTO> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
}