package com.justorder.backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.justorder.backend.dto.CuisineCategoryDTO;
import com.justorder.backend.model.CuisineCategory;
import com.justorder.backend.repository.CuisineCategoryRepository;

@RestController
@RequestMapping("/api/categories")
public class CuisineCategoryController {

    @Autowired
    private CuisineCategoryRepository categoryRepository;

    @GetMapping("/all")
    public ResponseEntity<List<CuisineCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<CuisineCategory> createCategory(@RequestBody CuisineCategoryDTO request) {
        CuisineCategory newCategory = new CuisineCategory(request.getName(), request.getDescription());
        return ResponseEntity.ok(categoryRepository.save(newCategory));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CuisineCategory> updateCategory(@PathVariable Long id, @RequestBody CuisineCategoryDTO request) {
        return categoryRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setDescription(request.getDescription());
            return ResponseEntity.ok(categoryRepository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}