package com.justorder.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justorder.backend.dto.AlergenDTO;
import com.justorder.backend.service.AlergenService;

/**
 * REST controller for managing Allergen entities.
 * Provides endpoints for performing CRUD (Create, Read, Update, Delete) 
 * operations on allergens within the system.
 */
@RestController
@RequestMapping("/api/alergens")
public class AlergenController {

    @Autowired
    private AlergenService alergenService;

    /**
     * Retrieves a list of all available allergens in the database.
     * @return a ResponseEntity containing a list of {@link AlergenDTO} objects.
     */
    @GetMapping
    public ResponseEntity<List<AlergenDTO>> getAllAlergens() {
        return ResponseEntity.ok(alergenService.getAllAlergens());
    }

    /**
     * Creates a new allergen.
     * @param request the data transfer object containing the details of the allergen.
     * @return a ResponseEntity containing the newly created {@link AlergenDTO}.
     */
    @PostMapping
    public ResponseEntity<AlergenDTO> createAlergen(@RequestBody AlergenDTO request) {
        return ResponseEntity.ok(alergenService.createAlergen(request));
    }

    /**
     * Updates an existing allergen identified by its ID.
     * @param id the unique identifier of the allergen to be updated.
     * @param request the data transfer object containing the updated details.
     * @return a ResponseEntity containing the updated {@link AlergenDTO}.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlergenDTO> updateAlergen(@PathVariable Long id, @RequestBody AlergenDTO request) {
        return ResponseEntity.ok(alergenService.updateAlergen(id, request));
    }

    /**
     * Deletes a specific allergen by its ID.
     * @param id the unique identifier of the allergen to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if successful.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlergen(@PathVariable Long id) {
        alergenService.deleteAlergen(id);
        return ResponseEntity.ok().build();
    }
}
