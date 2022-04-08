package nl.dimensiontech.domotics.chargerservice.message.handler;

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
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarMessageHandler implements MessageHandler {

    private final CarService carService;

    @ServiceActivator(inputChannel = "carStateInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {

        Long carId = TopicNameUtil.getCarId(message);
        String topicName = TopicNameUtil.getLastTopicName(message);
        String payload = String.valueOf(message.getPayload());

        if (!StringUtils.hasText(payload)) {
            log.debug("Message on topic '{}' does not contain a payload!", topicName);
            return;
        }

        Car car = carService.getCarById(carId).orElse(createCar(carId));

        switch (topicName) {
            case STATE_TOPIC:
                getCarState(payload).ifPresent(car::setCarState);
                break;
            case CHARGER_POWER_TOPIC:
                int chargerPower = Integer.parseInt(payload);
                car.setChargerPower(chargerPower);
                break;
            case LATITUDE_TOPIC:
                double latitude = Double.parseDouble(payload);
                car.setLatitude(latitude);
                break;
            case LONGITUDE_TOPIC:
                double longitude = Double.parseDouble(payload);
                car.setLongitude(longitude);
                break;
            case ODOMETER_TOPIC:
                int odometer = getOdometer(payload);
                car.setOdometer(odometer);
                break;
            case DISPLAY_NAME_TOPIC:
                car.setName(payload);
                break;
            default:
                log.debug("Not interested in messages from topic '{}'", topicName);
        }

        carService.handleStateChange(car);
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
