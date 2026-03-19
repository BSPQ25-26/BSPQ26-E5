package com.justorder.backend.model;

import java.util.ArrayList;
import java.util.List;

import com.justorder.backend.dto.RiderDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "riders")
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dni;
    private String name;
    private String phoneNumber;
    private String email;
    private String password;

    @OneToMany(mappedBy = "rider")
    private List<Order> orders = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "starter_point_id", nullable=false)
    private Localization starterPoint;

    public Rider() {
    }
    public Rider(String name, String dni, String phoneNumber, String email, String password, Localization starterPoint) {
        this.name = name;
        this.dni = dni;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.starterPoint = starterPoint;
    }
    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDni() { return dni; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<Order> getOrders() { return orders; }
    public Localization getStarterPoint() { return starterPoint; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDni(String dni) { this.dni = dni; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    public void setStarterPoint(Localization starterPoint) { this.starterPoint = starterPoint; }

    // toDTO
    public RiderDTO toDTO() {
        RiderDTO dto = new RiderDTO(
            this.id, this.name, this.dni, this.phoneNumber, this.email, this.password, null, this.starterPoint.toDTO()
        );
        dto.setOrderIds(this.orders.stream().map(Order::getId).toList());
        return dto;
    }
}
