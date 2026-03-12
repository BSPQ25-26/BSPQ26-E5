package com.justorder.backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToMany
    @JoinTable(
        name = "order_dishes",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "status_id")
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    private double totalPrice;
    private String secretCode;

    // Constructors
    public Order() {
    }

    public Order(Customer customer, List<Dish> dishes, OrderStatus status, Rider rider, double totalPrice, String secretCode) {
        this.customer = customer;
        this.dishes = dishes;
        this.status = status;
        this.rider = rider;
        this.totalPrice = totalPrice;
        this.secretCode = secretCode;
    }

    // Getters
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Dish> getDishes() { return dishes; }
    public OrderStatus getStatus() { return status; }
    public Rider getRider() { return rider; }
    public double getTotalPrice() { return totalPrice; }
    public String getSecretCode() { return secretCode; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setDishes(List<Dish> dishes) { this.dishes = dishes; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setRider(Rider rider) { this.rider = rider; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }
}
