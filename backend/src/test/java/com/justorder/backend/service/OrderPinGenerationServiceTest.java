package com.justorder.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class OrderPinGenerationServiceTest {

    private final OrderPinGenerationService orderPinGenerationService = new OrderPinGenerationService();

    @Test
    void generatePinReturnsSixDigitNumericValue() {
        String pin = orderPinGenerationService.generatePin();

        assertNotNull(pin);
        assertEquals(6, pin.length());
        assertTrue(pin.matches("\\d{6}"));
    }

    @Test
    void generatePinProducesVariableResults() {
        Set<String> generatedPins = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            generatedPins.add(orderPinGenerationService.generatePin());
        }

        assertTrue(generatedPins.size() > 1);
    }
}
