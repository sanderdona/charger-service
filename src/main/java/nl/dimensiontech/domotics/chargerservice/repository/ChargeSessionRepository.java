package nl.dimensiontech.domotics.chargerservice.repository;

import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChargeSessionRepository extends CrudRepository<ChargeSession, Long> {

    List<ChargeSession> findAllByOrderByIdAsc();

    Optional<ChargeSession> findByEndedAtIsNull();

    List<ChargeSession> findAllByEndedAtBetweenOrderByIdAsc(LocalDateTime from, LocalDateTime to);
}
