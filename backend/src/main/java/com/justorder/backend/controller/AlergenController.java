package com.justorder.backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.justorder.backend.dto.AlergenDTO;
import com.justorder.backend.model.Alergen;
import com.justorder.backend.repository.AlergenRepository;

@RestController
@RequestMapping("/api/alergens")
public class AlergenController {

    @Autowired
    private AlergenRepository alergenRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Alergen>> getAllAlergens() {
        return ResponseEntity.ok(alergenRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Alergen> createAlergen(@RequestBody AlergenDTO request) {
        Alergen newAlergen = new Alergen(request.getName(), request.getDescription());
        return ResponseEntity.ok(alergenRepository.save(newAlergen));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Alergen> updateAlergen(@PathVariable Long id, @RequestBody AlergenDTO request) {
        return alergenRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setDescription(request.getDescription());
            return ResponseEntity.ok(alergenRepository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAlergen(@PathVariable Long id) {
        if (alergenRepository.existsById(id)) {
            alergenRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}