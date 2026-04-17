package com.justorder.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class OrderPinSecurityService {

    private final PasswordEncoder passwordEncoder;

    public OrderPinSecurityService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String hashPin(String plainPin) {
        return passwordEncoder.encode(plainPin);
    }

    public boolean matches(String plainPin, String pinHash) {
        return passwordEncoder.matches(plainPin, pinHash);
    }
}
