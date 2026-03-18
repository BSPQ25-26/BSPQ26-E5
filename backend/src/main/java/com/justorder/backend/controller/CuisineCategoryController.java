package com.justorder.backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.justorder.backend.dto.CuisineCategoryDTO;
import com.justorder.backend.model.CuisineCategory;
import com.justorder.backend.repository.CuisineCategoryRepository;

/**
 * REST controller for managing Cuisine Category entities.
 * Provides endpoints for performing CRUD (Create, Read, Update, Delete) 
 * operations on cuisine categories within the system.
 * * @version 1.0
 */
@RestController
@RequestMapping("/api/categories")
public class CuisineCategoryController {

    @Autowired
    private CuisineCategoryRepository categoryRepository;

    /**
     * Retrieves a list of all available cuisine categories in the database.
     * * @return a ResponseEntity containing a list of {@link CuisineCategory} objects and an HTTP 200 OK status.
     */
    @GetMapping("/all")
    public ResponseEntity<List<CuisineCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    /**
     * Creates a new cuisine category and saves it to the database.
     * * @param request the data transfer object containing the details of the category to be created.
     * @return a ResponseEntity containing the newly created {@link CuisineCategory} and an HTTP 200 OK status.
     */
    @PostMapping("/create")
    public ResponseEntity<CuisineCategory> createCategory(@RequestBody CuisineCategoryDTO request) {
        CuisineCategory newCategory = new CuisineCategory(request.getName(), request.getDescription());
        return ResponseEntity.ok(categoryRepository.save(newCategory));
    }

    /**
     * Updates an existing cuisine category identified by its ID.
     * * @param id the unique identifier of the cuisine category to be updated.
     * @param request the data transfer object containing the updated details.
     * @return a ResponseEntity containing the updated {@link CuisineCategory} if found, 
     * or an HTTP 404 Not Found status if the category does not exist.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<CuisineCategory> updateCategory(@PathVariable Long id, @RequestBody CuisineCategoryDTO request) {
        return categoryRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setDescription(request.getDescription());
            return ResponseEntity.ok(categoryRepository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific cuisine category by its ID.
     * * @param id the unique identifier of the cuisine category to be deleted.
     * @return a ResponseEntity with an HTTP 200 OK status if the deletion was successful, 
     * or an HTTP 404 Not Found status if the category does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}