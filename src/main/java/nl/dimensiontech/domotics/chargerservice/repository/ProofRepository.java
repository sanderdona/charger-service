package nl.dimensiontech.domotics.chargerservice.repository;

import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ProofRepository extends CrudRepository<Proof, Long> {

    Optional<Proof> findByDate(LocalDate date);
}
