package com.rest.emergencyconnectuae.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String action;
    private String detail;
    private String ipAddress;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog(String username, String action, String detail, String ipAddress) {
        this.username = username;
        this.action = action;
        this.detail = detail;
        this.ipAddress = ipAddress;
    }
}