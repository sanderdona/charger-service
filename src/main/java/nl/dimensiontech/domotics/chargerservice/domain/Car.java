package nl.dimensiontech.domotics.chargerservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Car {

    @Id
    private Long id;

    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private String name;

    @Enumerated(EnumType.STRING)
    private CarState carState;

    private int chargerPower;

    private int odometer;

    private double latitude;

    private double longitude;

}
