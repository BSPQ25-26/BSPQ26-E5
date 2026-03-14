package com.justorder.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.justorder.backend.model.Localization;

public interface LocalizationRepository extends JpaRepository<Localization, Long> {
}
