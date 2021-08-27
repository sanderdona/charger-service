package nl.dimensiontech.domotics.chargerservice.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
@Data
public class Car {

    @Id
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CarState carState;

    private double latitude;

    private double longitude;

}
