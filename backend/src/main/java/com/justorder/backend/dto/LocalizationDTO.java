package com.justorder.backend.dto;

public class LocalizationDTO {

    private Long id;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String number;
    private double longitude;
    private double latitude;

    public LocalizationDTO() {
    }

    public LocalizationDTO(Long id, String city, String province, String country, String postalCode, String number, double longitude, double latitude) {
        this.id = id;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.number = number;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}