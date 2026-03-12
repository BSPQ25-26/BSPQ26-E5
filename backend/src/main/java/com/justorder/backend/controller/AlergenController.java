package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justorder.backend.dto.AlergenDTO;
import com.justorder.backend.service.AlergenService;

@RestController
@RequestMapping("/api/alergens")
public class AlergenController {

    @Autowired
    private AlergenService alergenService;

    // GET /api/alergens devuelve la lista de alérgenos disponibles
    @GetMapping
    public ResponseEntity<List<AlergenDTO>> getAllAlergens() {
        return ResponseEntity.ok(alergenService.getAllAlergens());
    }
}
