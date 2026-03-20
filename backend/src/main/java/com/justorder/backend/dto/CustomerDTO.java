package com.justorder.backend.dto;

import java.util.List;

public class CustomerDTO {
    
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private Integer age;
    private String dni;
    private List<LocalizationDTO> localizations;
    private List<String> allergenNames;
    private List<String> preferenceNames;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String name, String email, String phone, String password, Integer age, String dni) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.age = age;
        this.dni = dni;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public Integer getAge() { return age; }
    public String getDni() { return dni; }
    public List<LocalizationDTO> getLocalizations() { return localizations; }
    public List<String> getAllergenNames() { return allergenNames; }
    public List<String> getPreferenceNames() { return preferenceNames; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setAge(Integer age) { this.age = age; }
    public void setDni(String dni) { this.dni = dni; }
    public void setLocalizations(List<LocalizationDTO> localizations) { this.localizations = localizations; }
    public void setAllergenNames(List<String> allergenNames) { this.allergenNames = allergenNames; }
    public void setPreferenceNames(List<String> preferenceNames) { this.preferenceNames = preferenceNames; }
}