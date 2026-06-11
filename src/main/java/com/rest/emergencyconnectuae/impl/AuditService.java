package com.rest.emergencyconnectuae.impl;

import com.rest.emergencyconnectuae.models.AuditLog;
import com.rest.emergencyconnectuae.repo.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String username, String action, String detail, String ip) {
        auditLogRepository.save(new AuditLog(username, action, detail, ip));
    }

    public Page<AuditLog> getAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<AuditLog> getByUser(String username, Pageable pageable) {
        return auditLogRepository.findByUsername(username, pageable);
    }
}
