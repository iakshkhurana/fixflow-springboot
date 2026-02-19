package com.fixflow.controller;

import com.fixflow.model.Building;
import com.fixflow.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Provides building information.
 * Used by the registration form and request creation form to populate dropdowns.
 */
@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingRepository buildingRepo;

    // GET /api/buildings — list all buildings (for dropdowns)
    @GetMapping
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingRepo.findAll());
    }

    // GET /api/buildings/{id} — get a specific building
    @GetMapping("/{id}")
    public ResponseEntity<Building> getById(@PathVariable Long id) {
        return buildingRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
