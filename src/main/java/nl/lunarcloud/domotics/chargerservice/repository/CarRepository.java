package nl.lunarcloud.domotics.chargerservice.repository;

import nl.lunarcloud.domotics.chargerservice.domain.Car;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CarRepository extends CrudRepository<Car, Long> {

    Optional<Car> findByUuid(UUID uuid);
}
