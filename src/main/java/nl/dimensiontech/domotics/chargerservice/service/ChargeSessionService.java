package nl.dimensiontech.domotics.chargerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import nl.dimensiontech.domotics.chargerservice.repository.ChargeSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargeSessionService {

    private final EnergyMeterService energyMeterService;
    private final ChargeSessionRepository chargeSessionRepository;

    public void handleChargePowerUpdate(float chargePower) {
        if (isCharging(chargePower) && !isChargeSessionActive()) {
            startNewSession();
        }

        if (!isCharging(chargePower) && isChargeSessionActive()) {
            endSession();
        }
    }

    private void startNewSession() {
        final float currentReading = energyMeterService.getCurrentReading();

        log.info("starting new session at {} kWh", currentReading);

        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setStartkWh(currentReading);
        chargeSession.setChargeSessionType(ChargeSessionType.ANONYMOUS);
        ChargeSession savedSession = chargeSessionRepository.save(chargeSession);

        log.info("started session with id {}", savedSession.getId());
    }

    private boolean isCharging(float power) {
        return power > 0;
    }

    private void endSession() {
        ChargeSession chargeSession = chargeSessionRepository
                .findByEndedAtIsNull()
                .orElseThrow(() -> new IllegalStateException("Cannot find active session!"));

        float currentReading = energyMeterService.getCurrentReading();

        log.info("Ending session with id {} at {} kWh", chargeSession.getId(), currentReading);

        float startKwh = chargeSession.getStartkWh();
        float chargedKwh = currentReading - startKwh;

        if (startKwh == currentReading) {
            log.info("Session will be deleted as it started at {} kWh which equals current reading of {} kWh",
                    startKwh, currentReading);
            chargeSessionRepository.delete(chargeSession);
        } else {
            chargeSession.setEndkWh(currentReading);
            chargeSession.setTotalkwH(chargedKwh);
            chargeSession.setEndedAt(LocalDateTime.now());
            chargeSessionRepository.save(chargeSession);

            log.info("Session with id {} ended - added {} kWh", chargeSession.getId(), chargedKwh);
        }
    }

    public void assignToActiveSession(Car car) {
        ChargeSession activeSession = getActiveSession().orElseThrow(() -> {
            throw new CannotAssignException("Cannot find a charge session to assign car to");
        });

        if (activeSession.getCar() == null) {
            activeSession.setCar(car);
            activeSession.setOdoMeter(car.getOdometer());
            activeSession.setChargeSessionType(ChargeSessionType.REGISTERED);

            chargeSessionRepository.save(activeSession);

            log.info("Assigned car {} to active session", car.getName());
        } else {
            log.warn("Active session is already assigned to car '{}'", car.getName());
        }
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

    public List<ChargeSession> getSessionsInRange(LocalDateTime startDate, LocalDateTime endDate) {
        return getSessionsInRange(startDate, endDate, false);
    }

    public List<ChargeSession> getSessionsInRange(LocalDateTime startDate, LocalDateTime endDate, boolean filterZeroUsage) {
        List<ChargeSession> sessions = chargeSessionRepository.findAllByEndedAtBetweenOrderByIdAsc(startDate, endDate);

        if (filterZeroUsage) {
            return sessions.stream()
                    .filter(chargeSession -> chargeSession.getStartkWh() != chargeSession.getEndkWh())
                    .collect(Collectors.toList());
        }
        return sessions;
    }

}
