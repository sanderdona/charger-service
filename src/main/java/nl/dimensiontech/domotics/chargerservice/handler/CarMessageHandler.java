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

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarMessageHandler implements MessageHandler {

    private static final String STATE_TOPIC = "state";
    private static final String LATITUDE_TOPIC = "latitude";
    private static final String LONGITUDE_TOPIC = "longitude";

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
            car.setCarState(CarState.valueOf(state.toUpperCase()));
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

        carService.handleStateChange(car);
    }

    private Car createCar(Long carId) {
        Car car = new Car();
        car.setId(carId);
        return carService.saveCar(car);
    }
}
