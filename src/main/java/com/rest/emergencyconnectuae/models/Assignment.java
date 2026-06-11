package com.rest.emergencyconnectuae.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter @Setter @NoArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private EmergencyUnit unit;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    private String assignedBy;

    public Assignment(Incident incident, EmergencyUnit unit, String assignedBy) {
        this.incident = incident;
        this.unit = unit;
        this.assignedBy = assignedBy;
    }
}