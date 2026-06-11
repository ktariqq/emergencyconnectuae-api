package com.rest.emergencyconnectuae.repo;

import com.rest.emergencyconnectuae.models.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Page<Assignment> findByIncidentId(Long incidentId, Pageable pageable);
    Page<Assignment> findAll(Pageable pageable);
}