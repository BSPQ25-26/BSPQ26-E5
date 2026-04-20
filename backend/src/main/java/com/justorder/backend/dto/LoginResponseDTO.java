package com.justorder.backend.dto;

public class LoginResponseDTO {
    private String token;
    private RiderDTO rider;
    private CustomerDTO customer;
    private RestaurantDTO restaurant;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    // Getters
    public String getToken() { return token; }
    public RiderDTO getRider() { return rider; }
    public CustomerDTO getCustomer() { return customer; }
    public RestaurantDTO getRestaurant() { return restaurant; }

    // Setters
    public void setToken(String token) { this.token = token; }
    public void setRider(RiderDTO rider) { this.rider = rider; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }
    public void setRestaurant(RestaurantDTO restaurant) { this.restaurant = restaurant; }
}
