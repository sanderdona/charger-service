package nl.dimensiontech.domotics.chargerservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.CarState;
import nl.dimensiontech.domotics.chargerservice.service.CarService;
import nl.dimensiontech.domotics.chargerservice.util.TopicNameUtil;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChargeMessageHandler implements MessageHandler {

    private final CarService carService;

    @ServiceActivator(inputChannel = "carStateInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {

        Long carId = TopicNameUtil.getCarId(message);
        Optional<Car> optionalCar = carService.getCarById(carId);

        Car car;
        if (optionalCar.isEmpty()) {
            car = createCar(carId);
        } else {
            car = optionalCar.get();
        }

        if (STATE_TOPIC.equals(TopicNameUtil.getValueName(message))) {
            String state = (String) message.getPayload();
            log.debug("Received car state message '{}'", state);
            getCarState(state).ifPresent(car::setCarState);
        }

        if (PLUGGED_IN.equals(TopicNameUtil.getValueName(message))) {
            boolean pluggedIn = Boolean.parseBoolean((String) message.getPayload());
            log.debug("Received plugged_in message '{}'", pluggedIn);
            car.setPluggedIn(pluggedIn);
        }

        if (IS_PRECONDITIONING.equals(TopicNameUtil.getValueName(message))) {
            boolean isPreconditioning = Boolean.parseBoolean((String) message.getPayload());
            log.debug("Received is_preconditioning message '{}'", isPreconditioning);
            car.setPreconditioning(isPreconditioning);
        }

        if (LATITUDE_TOPIC.equals(TopicNameUtil.getValueName(message))) {
            double latitude = Double.parseDouble((String) message.getPayload());
            log.debug("Received car latitude message '{}'", latitude);
            car.setLatitude(latitude);
        }

        if (LONGITUDE_TOPIC.equals(TopicNameUtil.getValueName(message))) {
            double longitude = Double.parseDouble((String) message.getPayload());
            log.debug("Received car longitude message '{}'", longitude);
            car.setLongitude(longitude);
        }

        if (ODOMETER_TOPIC.equals(TopicNameUtil.getValueName(message))) {
            int odometer = getOdometer(message);
            car.setOdometer(odometer);
        }

        if (DISPLAY_NAME_TOPIC.equals(TopicNameUtil.getValueName(message))) {
            String displayName = (String) message.getPayload();
            car.setName(displayName);
        }

        carService.handleStateChange(car);
    }

    private int getOdometer(Message<?> message) {
        String payload = (String) message.getPayload();
        Number number;

        try {
            number = NumberFormat.getNumberInstance(Locale.US).parse(payload);
        } catch (ParseException e) {
            log.error("Cannot parse value");
            throw new IllegalArgumentException("Cannot parse odometer value!");
        }

        return number.intValue();
    }

    private Car createCar(Long carId) {
        Car car = new Car();
        car.setId(carId);
        return carService.saveCar(car);
    }

    private Optional<CarState> getCarState(String state) {
        try {
            return Optional.of(CarState.valueOf(state.toUpperCase()));
        } catch (IllegalArgumentException e) {
            log.error("I guess this is an unknown state: {}", state.toUpperCase());
            return Optional.empty();
        }
    }
}
