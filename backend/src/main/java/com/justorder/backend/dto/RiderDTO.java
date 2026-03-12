package com.justorder.backend.dto;

import java.util.List;

public class RiderDTO {

    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private List<Long> orderIds;

    public RiderDTO() {
    }

    public RiderDTO(Long id, String name, String phoneNumber, String email, String password) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<Long> getOrderIds() { return orderIds; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }
}
