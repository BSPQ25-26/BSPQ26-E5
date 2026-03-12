package com.justorder.backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.justorder.backend.dto.CustomerDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String password;
    private Integer age;
    private String dni; 

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Localization> localizations = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "customer_alergens",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "alergen_id")
    )
    private List<Alergen> alergens = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "customer_preferences",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "cuisine_category_id")
    )
    private List<CuisineCategory> preferences = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    // Constructors 
    public Customer() {
    }

    public Customer(String name, 
        String email, 
        String phone, 
        String password, 
        Integer age, 
        String dni, 
        List<Localization> localizations,
        List<CuisineCategory> preferences
    ) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.age = age;
        this.dni = dni;
        this.localizations = localizations;
        this.preferences = preferences;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public Integer getAge() { return age; }
    public String getDni() { return dni; }
    public List<Localization> getLocalizations() { return localizations; }
    public List<Alergen> getAlergens() { return alergens; }
    public List<CuisineCategory> getPreferences() { return preferences; }
    public List<Order> getOrders() { return orders; }

    // Setters
    public void setId(Long id) { this.id = id;}
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setAge(Integer age) { this.age = age; }
    public void setDni(String dni) { this.dni = dni; }
    public void setLocalizations(List<Localization> localizations) { this.localizations = localizations; }
    public void setAlergens(List<Alergen> alergens) { this.alergens = alergens; }
    public void setPreferences(List<CuisineCategory> preferences) { this.preferences = preferences; }
    public void setOrders(List<Order> orders) { this.orders = orders; }

    // toDTO
    public CustomerDTO toDTO() {
        CustomerDTO dto = new CustomerDTO(
            this.id, this.name, this.email, this.phone, this.password, this.age, this.dni
        );
        dto.setLocalizations(this.localizations.stream().map(Localization::toDTO).collect(Collectors.toList()));
        dto.setAlergenNames(this.alergens.stream().map(Alergen::getName).collect(Collectors.toList()));
        dto.setPreferenceNames(this.preferences.stream().map(CuisineCategory::getName).collect(Collectors.toList()));
        return dto;
    }
}
