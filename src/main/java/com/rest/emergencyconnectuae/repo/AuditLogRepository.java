package com.rest.emergencyconnectuae.repo;

import com.rest.emergencyconnectuae.models.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findAll(Pageable pageable);
    Page<AuditLog> findByUsername(String username, Pageable pageable);
}