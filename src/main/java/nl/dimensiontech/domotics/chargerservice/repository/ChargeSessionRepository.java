package nl.dimensiontech.domotics.chargerservice.repository;

import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChargeSessionRepository extends CrudRepository<ChargeSession, Long> {

    Optional<ChargeSession> findByEndedAtIsNull();

    List<ChargeSession> findByEndedAtDateBetween(LocalDate startDate, LocalDate endDate);
}
