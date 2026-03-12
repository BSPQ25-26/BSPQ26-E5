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

    public List<AlergenDTO> getAllAlergens() {
        return alergenRepository.findAll()
                .stream()
                .map(Alergen::toDTO)
                .collect(Collectors.toList());
    }
}
