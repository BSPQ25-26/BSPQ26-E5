package com.justorder.backend.dto;

import java.util.List;

/**
 * Request payload used to create an order during checkout.
 */
public class CheckoutOrderRequestDTO {

    private Long customerId;
    private List<Long> dishIds;
    private double clientTotal;
    private String paymentToken;

    /**
     * Creates an empty checkout request.
     */
    public CheckoutOrderRequestDTO() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getDishIds() {
        return dishIds;
    }

    public void setDishIds(List<Long> dishIds) {
        this.dishIds = dishIds;
    }

    public double getClientTotal() {
        return clientTotal;
    }

    public void setClientTotal(double clientTotal) {
        this.clientTotal = clientTotal;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
}
