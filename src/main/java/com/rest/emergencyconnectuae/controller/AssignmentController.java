package com.rest.emergencyconnectuae.controller;

import com.rest.emergencyconnectuae.impl.AssignmentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignments")
public class AssignmentController {

    private final AssignmentServiceImpl assignmentService;

    @Operation(summary = "Assign an emergency unit to an incident (with distributed lock)")
    @PostMapping
    public ResponseEntity<?> assign(
            @RequestParam Long incidentId,
            @RequestParam Long unitId,
            Authentication auth,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.assign(
                        incidentId, unitId, auth.getName(), request.getRemoteAddr()));
    }

    @Operation(summary = "Get all assignments (paginated)")
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(assignmentService.getAll(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get assignments for a specific incident")
    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<?> getByIncident(
            @PathVariable Long incidentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(assignmentService.getByIncident(
                incidentId, PageRequest.of(page, size)));
    }
}