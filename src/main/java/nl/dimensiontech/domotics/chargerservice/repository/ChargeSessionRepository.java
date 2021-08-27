package nl.dimensiontech.domotics.chargerservice.repository;

import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.Optional;

public interface ChargeSessionRepository extends CrudRepository<ChargeSession, Long> {

    Iterable<ChargeSession> findAllByEndedAt(Date date);

    Optional<ChargeSession> findByEndedAtIsNull();
}
