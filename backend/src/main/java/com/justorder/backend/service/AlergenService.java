package com.justorder.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justorder.backend.dto.AlergenDTO;
import com.justorder.backend.model.Alergen;
import com.justorder.backend.repository.AlergenRepository;

@Service
public class AlergenService {

    @Autowired
    private AlergenRepository alergenRepository;

    /**
     * Retrieves all allergens and converts them to DTOs.
     */
    public List<AlergenDTO> getAllAlergens() {
        return alergenRepository.findAll()
                .stream()
                .map(Alergen::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new allergen from a DTO and returns it as a DTO.
     */
    public AlergenDTO createAlergen(AlergenDTO dto) {
        Alergen alergen = new Alergen(dto.getName(), dto.getDescription());
        Alergen saved = alergenRepository.save(alergen);
        return saved.toDTO();
    }

    /**
     * Updates an existing allergen or throws an exception if not found.
     */
    public AlergenDTO updateAlergen(Long id, AlergenDTO dto) {
        return alergenRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            Alergen updated = alergenRepository.save(existing);
            return updated.toDTO();
        }).orElseThrow(() -> new RuntimeException("Allergen not found with id: " + id));
    }

    /**
     * Deletes an allergen by its ID.
     */
    public void deleteAlergen(Long id) {
        if (!alergenRepository.existsById(id)) {
            throw new RuntimeException("Allergen not found with id: " + id);
        }
        alergenRepository.deleteById(id);
    }
}