package com.justorder.backend.model;

import com.justorder.backend.dto.LocalizationDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "localizations")
public class Localization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String number;
    private double longitude;
    private double latitude;

    public Localization() {
    }

    public Localization(String city, String province, String country, String postalCode, String number, double longitude, double latitude) {
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.number = number;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Localization(LocalizationDTO dto) {
        this.city = dto.getCity();
        this.province = dto.getProvince();
        this.country = dto.getCountry();
        this.postalCode = dto.getPostalCode();
        this.number = dto.getNumber();
        this.longitude = dto.getLongitude();
        this.latitude = dto.getLatitude();
    }

    // Getters
    public Long getId() { return id; }
    public String getCity() { return city; }
    public String getProvince() { return province; }
    public String getCountry() { return country; }
    public String getPostalCode() { return postalCode; }
    public String getNumber() { return number; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCity(String city) { this.city = city; }
    public void setProvince(String province) { this.province = province; }
    public void setCountry(String country) { this.country = country; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setNumber(String number) { this.number = number; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    // toDTO
    public LocalizationDTO toDTO() {
        return new LocalizationDTO(this.id, this.city, this.province, this.country, this.postalCode, this.number, this.longitude, this.latitude);
    }
}
