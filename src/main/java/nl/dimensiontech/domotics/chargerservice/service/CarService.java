package nl.dimensiontech.domotics.chargerservice.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.CarState;
import nl.dimensiontech.domotics.chargerservice.repository.CarRepository;
import nl.dimensiontech.domotics.chargerservice.util.DistanceUtil;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Data
@Slf4j
@RequiredArgsConstructor
public class CarService {

    private final ConfigProperties config;
    private final ChargeSessionService chargeSessionService;
    private final CarRepository carRepository;

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public void handleStateChange(Car car) {
        carRepository.save(car);

        if (CarState.CHARGING.equals(car.getCarState()) || isPreconditioning(car)) {
            handleStateToCharging(car);
        }
    }

    public List<Car> getCars() {
        return Streamable.of(carRepository.findAll()).toList();
    }

    private void handleStateToCharging(Car car) {

        if (isAtHome(car)) {
            assignToActiveSession(car);
        } else {
            log.info("Car {} is charging elsewhere.", car.getName());
        }
    }

    private void assignToActiveSession(Car car) {
        log.info("Car {} is at home, assigning to active session.", car.getName());

        if (!chargeSessionService.assignToActiveSession(car)) {
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
            assigned = chargeSessionService.assignToActiveSession(car);
            retries++;
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

    private boolean isPreconditioning(Car car) {
        return car.isPreconditioning() && car.isPluggedIn();
    }

    public boolean isAtHome(Car car) {
        double currentLatitude = car.getLatitude();
        double currentLongitude = car.getLongitude();
        double homeLatitude = config.getLocationConfig().getHomeLatitude();
        double homeLongitude = config.getLocationConfig().getHomeLongitude();

        int maxDistanceFromHome = config.getLocationConfig().getMaxDistanceFromHome();

        double currentDistanceFromHome = DistanceUtil.distanceBetween(homeLatitude, homeLongitude, currentLatitude, currentLongitude);

        log.info("Car distance from home: {} m", String.format("%,.2f", currentDistanceFromHome));

        return currentDistanceFromHome <= maxDistanceFromHome;
    }
}
