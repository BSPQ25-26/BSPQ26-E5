package com.justorder.backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.justorder.backend.dto.AlergenDTO;
import com.justorder.backend.model.Alergen;
import com.justorder.backend.repository.AlergenRepository;

/**
 * REST controller for managing Allergen entities.
 * Provides endpoints for performing CRUD (Create, Read, Update, Delete) 
 * operations on allergens within the system.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/alergens")
public class AlergenController {

    @Autowired
    private AlergenRepository alergenRepository;

    /**
     * Retrieves a list of all available allergens in the database.
     * * @return a ResponseEntity containing a list of {@link Alergen} objects and an HTTP 200 OK status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Alergen>> getAllAlergens() {
        return ResponseEntity.ok(alergenRepository.findAll());
    }

    /**
     * Creates a new allergen and saves it to the database.
     * * @param request the data transfer object containing the details of the allergen to be created.
     * @return a ResponseEntity containing the newly created {@link Alergen} and an HTTP 200 OK status.
     */
    @PostMapping("/create")
    public ResponseEntity<Alergen> createAlergen(@RequestBody AlergenDTO request) {
        Alergen newAlergen = new Alergen(request.getName(), request.getDescription());
        return ResponseEntity.ok(alergenRepository.save(newAlergen));
    }

    /**
     * Updates an existing allergen identified by its ID.
     * * @param id the unique identifier of the allergen to be updated.
     * @param request the data transfer object containing the updated details.
     * @return a ResponseEntity containing the updated {@link Alergen} if found, 
     * or an HTTP 404 Not Found status if the allergen does not exist.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Alergen> updateAlergen(@PathVariable Long id, @RequestBody AlergenDTO request) {
        return alergenRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setDescription(request.getDescription());
            return ResponseEntity.ok(alergenRepository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific allergen by its ID.
     * * @param id the unique identifier of the allergen to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if the deletion was successful, 
     * or an HTTP 404 Not Found status if the allergen does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAlergen(@PathVariable Long id) {
        if (alergenRepository.existsById(id)) {
            alergenRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}