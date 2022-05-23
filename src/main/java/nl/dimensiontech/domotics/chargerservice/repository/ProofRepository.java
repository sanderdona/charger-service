package nl.dimensiontech.domotics.chargerservice.repository;

import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ProofRepository extends PagingAndSortingRepository<Proof, Long> {

    Optional<Proof> findByDate(LocalDate date);
}
