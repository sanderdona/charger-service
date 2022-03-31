package nl.dimensiontech.domotics.chargerservice.repository;

import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChargeSessionRepository extends PagingAndSortingRepository<ChargeSession, Long> {

    Optional<ChargeSession> findByEndedAtIsNull();

    List<ChargeSession> findAllByEndedAtBetweenOrderByIdAsc(LocalDateTime from, LocalDateTime to);
}
