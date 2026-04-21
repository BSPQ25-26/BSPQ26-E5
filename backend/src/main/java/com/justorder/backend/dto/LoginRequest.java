package com.justorder.backend.dto;

public class LoginRequest {
    private String type; // "rider", "customer", "restaurant"
    private String email;
    private String password;

    // Getters y Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}