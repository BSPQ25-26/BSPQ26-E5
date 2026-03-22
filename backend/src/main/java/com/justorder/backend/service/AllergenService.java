package com.justorder.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justorder.backend.dto.AllergenDTO;
import com.justorder.backend.model.Allergen;
import com.justorder.backend.repository.AllergenRepository;

@Service
public class AllergenService {

    @Autowired
    private AllergenRepository allergenRepository;

    // Returns a list containing all the allergens
    public List<AllergenDTO> getAllAllergens() {
        return allergenRepository.findAll()
                .stream()
                .map(Allergen::toDTO)
                .collect(Collectors.toList());
    }
}
