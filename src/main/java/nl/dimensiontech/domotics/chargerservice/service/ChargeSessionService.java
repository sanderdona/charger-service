package nl.dimensiontech.domotics.chargerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import nl.dimensiontech.domotics.chargerservice.repository.ChargeSessionRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargeSessionService {

    private final EnergyMeterService energyMeterService;
    private final ChargeSessionRepository chargeSessionRepository;

    public void handleChargePowerUpdate(float chargePower) {
        if (!isChargeSessionActive() && isCharging(chargePower)) {
            startNewSession();
        }

        if (isChargeSessionActive() && !isCharging(chargePower)) {
            endSession();
        }
    }

    public void startNewSession() {

        checkValidState();

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

    private void checkValidState() {
        if (chargeSessionRepository.findByEndedAtIsNull().isPresent()) {
            throw new IllegalStateException("Session cannot be started; there is already a session active!");
        }
    }

    public void endSession() {
        ChargeSession chargeSession = chargeSessionRepository
                .findByEndedAtIsNull()
                .orElseThrow(() -> new IllegalStateException("Cannot find active session!"));

        float currentReading = energyMeterService.getCurrentReading();

        log.info("Ending session with id {} at {} kWh", chargeSession.getId(), currentReading);

        float startKwh = chargeSession.getStartkWh();
        float chargedKwh = currentReading - startKwh;
        chargeSession.setEndkWh(currentReading);
        chargeSession.setTotalkwH(chargedKwh);
        chargeSession.setEndedAt(LocalDateTime.now());

        chargeSessionRepository.save(chargeSession);

        log.info("Session with id {} ended - added {} kWh", chargeSession.getId(), chargedKwh);
    }

    public boolean assignToActiveSession(Car car) {
        Optional<ChargeSession> activeSession = getActiveSession();

        if (activeSession.isPresent() && activeSession.get().getCar() == null) {
            ChargeSession chargeSession = activeSession.get();
            chargeSession.setCar(car);
            chargeSession.setOdoMeter(car.getOdometer());
            chargeSession.setChargeSessionType(ChargeSessionType.REGISTERED);

            chargeSessionRepository.save(chargeSession);

            log.info("Assigned car {} to active session", car.getName());
            return true;

        } else {
            log.warn("Cannot assign car '{}' to session", car.getName());
            return false;
        }
    }

    public List<ChargeSession> getSessions() {
        return Streamable.of(chargeSessionRepository.findAll()).toList();
    }

    public boolean isChargeSessionActive() {
        return getActiveSession().isPresent();
    }

    private Optional<ChargeSession> getActiveSession() {
        return chargeSessionRepository.findByEndedAtIsNull();
    }

    public List<ChargeSession> getSessionsInRange(LocalDateTime startDate, LocalDateTime endDate) {
        return chargeSessionRepository.findAllByEndedAtBetween(startDate, endDate);
    }

}
