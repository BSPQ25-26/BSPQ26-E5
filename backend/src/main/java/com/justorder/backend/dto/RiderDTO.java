package com.justorder.backend.dto;

import java.util.List;

public class RiderDTO {

    private Long id;
    private String name;
    private String dni;
    private String phoneNumber;
    private String email;
    private String password;
    private List<Long> orderIds;
    private LocalizationDTO starterPoint;

    public RiderDTO() {
    }

    public RiderDTO(Long id, String name, String dni, String phoneNumber, String email, String password, List<Long> orderIds, LocalizationDTO starterPoint) {
        this.id = id;
        this.name = name;
        this.dni = dni;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.orderIds = orderIds;
        this.starterPoint = starterPoint;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDni() { return dni; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<Long> getOrderIds() { return orderIds; }
    public LocalizationDTO getStarterPoint() { return starterPoint; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDni(String dni) { this.dni = dni; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }
    public void setStarterPoint(LocalizationDTO starterPoint) { this.starterPoint = starterPoint; }
}