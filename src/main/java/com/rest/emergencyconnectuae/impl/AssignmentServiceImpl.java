package com.rest.emergencyconnectuae.impl;

import com.rest.emergencyconnectuae.models.Assignment;
import com.rest.emergencyconnectuae.models.EmergencyUnit;
import com.rest.emergencyconnectuae.models.Incident;
import com.rest.emergencyconnectuae.redis.CacheService;
import com.rest.emergencyconnectuae.redis.LockService;
import com.rest.emergencyconnectuae.repo.AssignmentRepository;
import com.rest.emergencyconnectuae.repo.EmergencyUnitRepository;
import com.rest.emergencyconnectuae.repo.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl {

    private final AssignmentRepository assignmentRepository;
    private final IncidentRepository incidentRepository;
    private final EmergencyUnitRepository unitRepository;
    private final LockService lockService;
    private final CacheService cacheService;
    private final AuditService auditService;

    public Assignment assign(Long incidentId, Long unitId, String requester, String ip) {
        // Redis distributed lock prevents race condition on unit assignment
        String lockKey = "unit:" + unitId;
        return lockService.executeWithLock(lockKey, () -> {
            EmergencyUnit unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new RuntimeException("Unit not found: " + unitId));

            if (unit.getStatus() != EmergencyUnit.UnitStatus.AVAILABLE)
                throw new RuntimeException("Unit is not available.");

            Incident incident = incidentRepository.findById(incidentId)
                    .orElseThrow(() -> new RuntimeException("Incident not found: " + incidentId));

            unit.setStatus(EmergencyUnit.UnitStatus.DEPLOYED);
            unitRepository.save(unit);

            incident.setStatus(Incident.Status.IN_PROGRESS);
            incidentRepository.save(incident);

            Assignment assignment = assignmentRepository.save(
                    new Assignment(incident, unit, requester));

            cacheService.evict("incidents:active");
            cacheService.evict("units:available");
            auditService.log(requester, "UNIT_ASSIGNED",
                    "Unit " + unitId + " -> Incident " + incidentId, ip);

            return assignment;
        });
    }

    public Page<Assignment> getAll(Pageable pageable) {
        return assignmentRepository.findAll(pageable);
    }

    public Page<Assignment> getByIncident(Long incidentId, Pageable pageable) {
        return assignmentRepository.findByIncidentId(incidentId, pageable);
    }
}