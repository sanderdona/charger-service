package nl.dimensiontech.domotics.chargerservice.domain;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
@Data
public class Car {

    @Id
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CarState carState;

    private int chargerPower;

    private int odometer;

    private double latitude;

    private double longitude;

}
