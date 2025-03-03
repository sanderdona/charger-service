package nl.lunarcloud.domotics.chargerservice.messaging.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.domain.Car;
import nl.lunarcloud.domotics.chargerservice.domain.CarState;
import nl.lunarcloud.domotics.chargerservice.service.CarService;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static nl.lunarcloud.domotics.chargerservice.common.util.TopicNameUtil.getLastTopicName;
import static nl.lunarcloud.domotics.chargerservice.messaging.constants.TopicNameConstants.*;
import static nl.lunarcloud.domotics.chargerservice.common.util.TopicNameUtil.getCarId;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarMessageHandler implements MessageHandler {

    private final CarService carService;

    @ServiceActivator(inputChannel = "carStateInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {

        try {
            final var carId = getCarId(message);
            final var topicName = getLastTopicName(message);
            final var payload = String.valueOf(message.getPayload());

            if (!StringUtils.hasText(payload)) {
                log.debug("Message on topic '{}' does not contain a payload!", topicName);
                return;
            }

            Car car = carService.getCarById(carId).orElseGet(() -> createCar(carId));

            switch (topicName) {
                case DISPLAY_NAME_TOPIC -> car.setName(payload);
                case STATE_TOPIC -> getCarState(payload).ifPresent(car::setCarState);
                case ODOMETER_TOPIC -> car.setOdometer(getOdometer(payload));
                case CHARGER_POWER_TOPIC -> car.setChargerPower(Integer.parseInt(payload));
                case LATITUDE_TOPIC -> car.setLatitude(Double.parseDouble(payload));
                case LONGITUDE_TOPIC -> car.setLongitude(Double.parseDouble(payload));
                default -> log.debug("Not interested in messages on topic '{}'", topicName);
            }

            carService.handleStateChange(car);
        } catch (Exception e) {
            log.error("Failed handling received message", e);
        }
    }

    private int getOdometer(String payload) {
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
        car.setUuid(UUID.randomUUID());
        return carService.saveCar(car);
    }

    private Optional<CarState> getCarState(String state) {
        try {
            return Optional.of(CarState.valueOf(state.toUpperCase()));
        } catch (IllegalArgumentException e) {
            log.warn("I guess this is an unknown state: {}", state.toUpperCase());
            return Optional.empty();
        }
    }
}
