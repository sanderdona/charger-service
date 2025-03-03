package nl.lunarcloud.domotics.chargerservice.repository;

import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChargeSessionRepository extends PagingAndSortingRepository<ChargeSession, Long>, ListCrudRepository<ChargeSession, Long> {
    Optional<ChargeSession> findByUuid(UUID uuid);

    Optional<ChargeSession> findByEndedAtIsNull();

    List<ChargeSession> findAllByEndedAtBetweenOrderByIdAsc(LocalDateTime from, LocalDateTime to);
}
