package nl.lunarcloud.domotics.chargerservice.service;

import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.domain.Car;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSessionType;
import nl.lunarcloud.domotics.chargerservice.common.exception.CannotAssignException;
import nl.lunarcloud.domotics.chargerservice.events.ChargeSessionSavedEvent;
import nl.lunarcloud.domotics.chargerservice.repository.ChargeSessionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChargeSessionService {

    private final EnergyMeterService energyMeterService;
    private final ChargeSessionRepository chargeSessionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ChargeSessionService(EnergyMeterService energyMeterService, ChargeSessionRepository chargeSessionRepository,
                                ApplicationEventPublisher eventPublisher) {
        this.energyMeterService = energyMeterService;
        this.chargeSessionRepository = chargeSessionRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handleChargePowerUpdate(float chargePower) {
        if (isCharging(chargePower) && !isChargeSessionActive()) {
            startNewSession();
        }

        if (!isCharging(chargePower) && isChargeSessionActive()) {
            endSession();
        }
    }

    private boolean isCharging(float chargePower) {
        return chargePower > 0;
    }

    private void startNewSession() {
        double currentReading = energyMeterService.getCurrentReading();

        log.info("starting new session at {} kWh", currentReading);

        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setUuid(UUID.randomUUID());
        chargeSession.setStartKwh(currentReading);
        ChargeSession savedSession = chargeSessionRepository.save(chargeSession);
        eventPublisher.publishEvent(new ChargeSessionSavedEvent(savedSession));

        log.info("started session with id {}", savedSession.getId());
    }

    private void endSession() {
        ChargeSession activeChargeSession = chargeSessionRepository
                .findByEndedAtIsNull()
                .orElseThrow(() -> new IllegalStateException("Cannot find active session!"));

        BigDecimal currentReading = BigDecimal.valueOf(energyMeterService.getCurrentReading());

        log.info("Ending session with id {} at {} kWh", activeChargeSession.getId(), currentReading);

        BigDecimal startKwh = BigDecimal.valueOf(activeChargeSession.getStartKwh());
        BigDecimal chargedKwh = currentReading.subtract(startKwh);

        if (startKwh.equals(currentReading)) {
            log.info("Session will be saved as a zero usage session");
        }

        activeChargeSession.setEndedAt(LocalDateTime.now());
        activeChargeSession.setEndKwh(currentReading.doubleValue());
        activeChargeSession.setTotalKwh(chargedKwh.doubleValue());

        chargeSessionRepository.save(activeChargeSession);

        eventPublisher.publishEvent(new ChargeSessionSavedEvent(activeChargeSession));

        log.info("Session with id {} ended - added {} kWh", activeChargeSession.getId(), chargedKwh);
    }

    public void assignToActiveSession(Car car) {
        ChargeSession activeSession = getActiveSession().orElseThrow(
                () -> new CannotAssignException("Cannot find a charge session to assign car to")
        );

        if (activeSession.getCar() == null) {
            activeSession.setCar(car);
            activeSession.setOdoMeter(car.getOdometer());
            activeSession.setChargeSessionType(ChargeSessionType.REGISTERED);

            chargeSessionRepository.save(activeSession);
            eventPublisher.publishEvent(new ChargeSessionSavedEvent(activeSession));

            log.info("Assigned car {} to active session", car.getName());
        } else {
            log.warn("Active session is already assigned to car '{}'", car.getName());
        }
    }

    public Optional<ChargeSession> getSessionById(UUID id) {
        return chargeSessionRepository.findByUuid(id);
    }

    public Page<ChargeSession> getSessions(Pageable pageable) {
        return chargeSessionRepository.findAll(pageable);
    }

    public boolean isChargeSessionActive() {
        return getActiveSession().isPresent();
    }

    private Optional<ChargeSession> getActiveSession() {
        return chargeSessionRepository.findByEndedAtIsNull();
    }

    public List<ChargeSession> getSessionsInRange(LocalDateTime startDate, LocalDateTime endDate, boolean filterZeroUsage) {
        List<ChargeSession> sessions = chargeSessionRepository.findAllByEndedAtBetweenOrderByIdAsc(startDate, endDate);

        if (filterZeroUsage) {
            return sessions.stream()
                    .filter(chargeSession -> !chargeSession.getStartKwh().equals(chargeSession.getEndKwh()))
                    .collect(Collectors.toList());
        }
        return sessions;
    }

}
