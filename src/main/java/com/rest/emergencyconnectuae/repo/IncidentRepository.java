package com.rest.emergencyconnectuae.repo;

import com.rest.emergencyconnectuae.models.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    Page<Incident> findAll(Pageable pageable);
    Page<Incident> findByStatus(Incident.Status status, Pageable pageable);
    Page<Incident> findByRegion(String region, Pageable pageable);
    List<Incident> findByStatus(Incident.Status status);
}