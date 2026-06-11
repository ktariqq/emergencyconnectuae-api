package com.rest.emergencyconnectuae.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rest.emergencyconnectuae.models.Incident;
import com.rest.emergencyconnectuae.redis.CacheService;
import com.rest.emergencyconnectuae.repo.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl {

    private final IncidentRepository incidentRepository;
    private final CacheService cacheService;
    private final AuditService auditService;

    private static final String ACTIVE_CACHE_KEY = "incidents:active";

    public Incident create(Incident incident, String requester, String ip) {
        Incident saved = incidentRepository.save(incident);
        cacheService.evict(ACTIVE_CACHE_KEY);
        auditService.log(requester, "INCIDENT_CREATED", "ID: " + saved.getId(), ip);
        return saved;
    }

    public Page<Incident> getAll(Pageable pageable) {
        return incidentRepository.findAll(pageable);
    }

    public Page<Incident> getByStatus(Incident.Status status, Pageable pageable) {
        return incidentRepository.findByStatus(status, pageable);
    }

    public Page<Incident> getByRegion(String region, Pageable pageable) {
        return incidentRepository.findByRegion(region, pageable);
    }

    public List<Incident> getActiveIncidents() {
        return cacheService.get(ACTIVE_CACHE_KEY, new TypeReference<List<Incident>>() {})
                .orElseGet(() -> {
                    List<Incident> active = incidentRepository.findByStatus(Incident.Status.OPEN);
                    cacheService.put(ACTIVE_CACHE_KEY, active, 60);
                    return active;
                });
    }

    public Incident updateStatus(Long id, Incident.Status status, String requester, String ip) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found: " + id));
        incident.setStatus(status);
        Incident saved = incidentRepository.save(incident);
        cacheService.evict(ACTIVE_CACHE_KEY);
        auditService.log(requester, "INCIDENT_STATUS_UPDATED",
                "ID: " + id + " => " + status, ip);
        return saved;
    }

    public Incident getById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found: " + id));
    }
}