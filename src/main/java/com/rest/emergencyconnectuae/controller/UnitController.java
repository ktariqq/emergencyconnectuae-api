package com.rest.emergencyconnectuae.controller;

import com.rest.emergencyconnectuae.impl.UnitServiceImpl;
import com.rest.emergencyconnectuae.models.EmergencyUnit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
@Tag(name = "Emergency Units")
public class UnitController {

    private final UnitServiceImpl unitService;

    @Operation(summary = "Register a new emergency unit")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EmergencyUnit unit) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unitService.create(unit));
    }

    @Operation(summary = "Get all units (paginated)")
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(unitService.getAll(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get available units — cached")
    @GetMapping("/available")
    public ResponseEntity<?> getAvailable() {
        return ResponseEntity.ok(unitService.getAvailable());
    }

    @Operation(summary = "Update unit status")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam EmergencyUnit.UnitStatus status) {
        return ResponseEntity.ok(unitService.updateStatus(id, status));
    }
}