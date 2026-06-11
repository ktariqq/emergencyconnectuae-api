package com.rest.emergencyconnectuae.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incidents")
@Getter @Setter @NoArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String location;

    @NotBlank
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private String reportedBy;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED
    }

    public enum Severity {
        LOW, HIGH, CRITICAL
    }

    public Incident(String title, String description, String location,
                    String region, Severity severity, String reportedBy) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.region = region;
        this.severity = severity;
        this.reportedBy = reportedBy;
    }
}