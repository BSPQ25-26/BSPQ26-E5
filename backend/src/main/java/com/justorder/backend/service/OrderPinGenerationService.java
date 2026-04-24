package com.justorder.backend.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class OrderPinGenerationService {

    private static final int PIN_UPPER_BOUND_EXCLUSIVE = 1_000_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generatePin() {
        int value = RANDOM.nextInt(PIN_UPPER_BOUND_EXCLUSIVE);
        return String.format("%06d", value);
    }
}
