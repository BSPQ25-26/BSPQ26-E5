package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.AllergenDTO;
import com.justorder.backend.service.AllergenService;

@RestController
@RequestMapping("/api/allergens")
public class AllergenController {

    @Autowired
    private AllergenService allergenService;

    // GET /api/allergens returns a list containing all the allergens
    @GetMapping
    public ResponseEntity<List<AllergenDTO>> getAllAllergens() {
        try {
            return ResponseEntity.ok(allergenService.getAllAllergens());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
