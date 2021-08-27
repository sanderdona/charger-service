package nl.dimensiontech.domotics.chargerservice.repository;

import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.CarState;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CarRepository extends CrudRepository<Car, Long> {

    Optional<Car> findByCarState(CarState carState);

}
