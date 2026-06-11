package com.rest.emergencyconnectuae.repo;

import com.rest.emergencyconnectuae.models.EmergencyUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyUnitRepository extends JpaRepository<EmergencyUnit, Long> {
    Page<EmergencyUnit> findAll(Pageable pageable);
    List<EmergencyUnit> findByStatusAndRegion(EmergencyUnit.UnitStatus status, String region);
    List<EmergencyUnit> findByStatus(EmergencyUnit.UnitStatus status);
}