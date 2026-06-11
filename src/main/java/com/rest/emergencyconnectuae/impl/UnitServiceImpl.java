package com.rest.emergencyconnectuae.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rest.emergencyconnectuae.models.EmergencyUnit;
import com.rest.emergencyconnectuae.redis.CacheService;
import com.rest.emergencyconnectuae.repo.EmergencyUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl {

    private final EmergencyUnitRepository unitRepository;
    private final CacheService cacheService;

    private static final String AVAILABLE_CACHE = "units:available";

    public EmergencyUnit create(EmergencyUnit unit) {
        EmergencyUnit saved = unitRepository.save(unit);
        cacheService.evict(AVAILABLE_CACHE);
        return saved;
    }

    public Page<EmergencyUnit> getAll(Pageable pageable) {
        return unitRepository.findAll(pageable);
    }

    public List<EmergencyUnit> getAvailable() {
        return cacheService.get(AVAILABLE_CACHE, new TypeReference<List<EmergencyUnit>>() {})
                .orElseGet(() -> {
                    List<EmergencyUnit> units =
                            unitRepository.findByStatus(EmergencyUnit.UnitStatus.AVAILABLE);
                    cacheService.put(AVAILABLE_CACHE, units, 30);
                    return units;
                });
    }

    public EmergencyUnit updateStatus(Long id, EmergencyUnit.UnitStatus status) {
        EmergencyUnit unit = unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found: " + id));
        unit.setStatus(status);
        EmergencyUnit saved = unitRepository.save(unit);
        cacheService.evict(AVAILABLE_CACHE);
        return saved;
    }
}