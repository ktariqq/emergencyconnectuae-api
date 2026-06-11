package com.rest.emergencyconnectuae.controller;

import com.rest.emergencyconnectuae.impl.IncidentServiceImpl;
import com.rest.emergencyconnectuae.models.Incident;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents")
public class IncidentController {

    private final IncidentServiceImpl incidentService;

    @Operation(summary = "Report a new emergency incident")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Incident incident,
                                    Authentication auth,
                                    HttpServletRequest request) {
        incident.setReportedBy(auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidentService.create(incident, auth.getName(), request.getRemoteAddr()));
    }

    @Operation(summary = "Get all incidents (paginated)")
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(incidentService.getAll(pageable));
    }

    @Operation(summary = "Get active (OPEN) incidents — cached")
    @GetMapping("/active")
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(incidentService.getActiveIncidents());
    }

    @Operation(summary = "Get incidents by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(
            @PathVariable Incident.Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(incidentService.getByStatus(status,
                PageRequest.of(page, size)));
    }

    @Operation(summary = "Get incidents by region")
    @GetMapping("/region/{region}")
    public ResponseEntity<?> getByRegion(
            @PathVariable String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(incidentService.getByRegion(region,
                PageRequest.of(page, size)));
    }

    @Operation(summary = "Get incident by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getById(id));
    }

    @Operation(summary = "Update incident status")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam Incident.Status status,
            Authentication auth,
            HttpServletRequest request) {
        return ResponseEntity.ok(incidentService.updateStatus(
                id, status, auth.getName(), request.getRemoteAddr()));
    }
}