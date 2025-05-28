package nl.lunarcloud.domotics.chargerservice.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Car {

    @Id
    private Long id;

    private UUID uuid;

    private String name;

    @Enumerated(EnumType.STRING)
    private CarState carState;

    private int chargerPower;

    private int odometer;

    private double latitude;

    private double longitude;
}
