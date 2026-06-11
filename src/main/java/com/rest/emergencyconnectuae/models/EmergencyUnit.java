package com.rest.emergencyconnectuae.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "emergency_units")
@Getter @Setter @NoArgsConstructor
public class EmergencyUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitStatus status = UnitStatus.AVAILABLE;

    @NotBlank
    private String region;

    public enum UnitType {
        AMBULANCE, FIRE_UNIT, POLICE_UNIT, RESCUE_TEAM
    }

    public enum UnitStatus {
        AVAILABLE, DEPLOYED, OFFLINE
    }

    public EmergencyUnit(String name, UnitType type, String region) {
        this.name = name;
        this.type = type;
        this.region = region;
    }
}