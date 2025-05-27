package nl.lunarcloud.domotics.chargerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.common.config.ConfigProperties;
import nl.lunarcloud.domotics.chargerservice.common.exception.CannotAssignException;
import nl.lunarcloud.domotics.chargerservice.repository.CarRepository;
import nl.lunarcloud.domotics.chargerservice.domain.Car;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static nl.lunarcloud.domotics.chargerservice.common.util.DistanceUtil.distanceBetween;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarService {

    private final ConfigProperties config;
    private final ChargeSessionService chargeSessionService;
    private final CarRepository carRepository;

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public Optional<Car> getCarByUuid(UUID id) {
        return carRepository.findByUuid(id);
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public void handleStateChange(Car newCarState) {
        Car oldCarState = carRepository.findById(newCarState.getId()).orElse(newCarState);
        carRepository.save(newCarState);

        if (isStateChangeToCharging(newCarState, oldCarState)) {
            handleStateToCharging(newCarState);
        }
    }

    private void handleStateToCharging(Car car) {

        if (isAtHome(car)) {
            assignToActiveSession(car);
        } else {
            log.info("Car {} is not charging at home. Skip assigning to session.", car.getName());
        }
    }

    private void assignToActiveSession(Car car) {
        log.info("Car {} is at home, assigning to active session.", car.getName());

        try {
            chargeSessionService.assignToActiveSession(car);
        } catch (CannotAssignException e) {
            retryAssignToActiveSession(car);
        }
    }

    private void retryAssignToActiveSession(Car car) {
        ConfigProperties.SessionAssignment sessionAssignment = config.getSessionAssignment();
        int retryTimeout = sessionAssignment.getRetryTimeout();
        int numberOfRetries = sessionAssignment.getNumberOfRetries();

        boolean assigned = false;
        int retries = 0;

        while (!assigned && retries < numberOfRetries) {
            log.info("Assigning to active session failed. Trying again in {} seconds...", retryTimeout);
            sleep(retryTimeout);
            try {
                chargeSessionService.assignToActiveSession(car);
                assigned = true;
            } catch (CannotAssignException e) {
                retries++;
            }
        }

        if (!assigned) {
            log.error("Could not assign to session after {} retries!", numberOfRetries);
        }
    }

    private void sleep(int timeInSeconds) {
        try {
            TimeUnit.SECONDS.sleep(timeInSeconds);
        } catch (InterruptedException e) {
            log.error("Sleep thread interrupted");
        }
    }

    private boolean isStateChangeToCharging(Car newCarState, Car oldCarState) {
        return oldCarState.getChargerPower() == 0 && newCarState.getChargerPower() > 0;
    }

    public boolean isAtHome(Car car) {
        double currentLatitude = car.getLatitude();
        double currentLongitude = car.getLongitude();
        double homeLatitude = config.getLocationConfig().getHomeLatitude();
        double homeLongitude = config.getLocationConfig().getHomeLongitude();

        int maxDistanceFromHome = config.getLocationConfig().getMaxDistanceFromHome();

        double currentDistanceFromHome = distanceBetween(homeLatitude, homeLongitude, currentLatitude, currentLongitude);

        log.info("Car distance from home: {} m", String.format("%,.2f", currentDistanceFromHome));

        return currentDistanceFromHome <= maxDistanceFromHome;
    }

}
