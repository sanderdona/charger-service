package nl.dimensiontech.domotics.chargerservice.handler;

import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.CarState;
import nl.dimensiontech.domotics.chargerservice.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.TOPIC_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarMessageHandlerTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private ChargeMessageHandler carMessageHandler;

    @Captor
    private ArgumentCaptor<Car> carCaptor;

    @BeforeEach
    public void init() {
        Car car = new Car();
        car.setCarState(CarState.ONLINE);
        when(carService.getCarById(1L)).thenReturn(Optional.of(car));
    }

    @Test
    public void shouldHandleCarStateMessage() {
        // given
        String topic = "teslamate/cars/1/state";
        String payload = "driving";

        Message<String> message = createMessage(topic, payload);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getCarState()).isEqualTo(CarState.DRIVING);
    }

    @Test
    public void shouldHandleCarChargerPowerMessage() {
        // given
        String topic = "teslamate/cars/1/charger_power";
        String payload = "2";

        Message<String> message = createMessage(topic, payload);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getChargerPower()).isEqualTo(2);
    }

    @Test
    public void shouldHandleCarLatitudeMessage() {
        // given
        String topic = "teslamate/cars/1/latitude";
        String payload = "51.012345";

        Message<String> message = createMessage(topic, payload);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getLatitude()).isEqualTo(51.012345);
    }

    @Test
    public void shouldHandleCarLongitudeMessage() {
        // given
        String topic = "teslamate/cars/1/longitude";
        String payload = "5.000000";

        Message<String> message = createMessage(topic, payload);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getLongitude()).isEqualTo(5.000000);
    }

    @Test
    public void shouldHandleCarOdometerMessage() {
        // given
        String topic = "teslamate/cars/1/odometer";
        String payload = "16119.19";

        Message<String> message = createMessage(topic, payload);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getOdometer()).isEqualTo(16119);
    }

    @Test
    public void shouldHandleCarDisplayNameMessage() {
        // given
        String topic = "teslamate/cars/1/display_name";
        String payload = "FooCar";

        Message<String> message = createMessage(topic, payload);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getName()).isEqualTo("FooCar");
    }

    @Test
    public void shouldHandleUnknownCarState() {
        // given
        String topic = "teslamate/cars/1/state";
        String payload = "foo";

        Message<String> message = createMessage(topic, payload);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getCarState()).isEqualTo(CarState.ONLINE);
    }

    @Test
    public void shouldCreateNewCar() {
        // given
        when(carService.getCarById(1L)).thenReturn(Optional.empty());
        Car newCar = new Car();
        when(carService.saveCar(isA(Car.class))).thenReturn(newCar);

        String topic = "teslamate/cars/1/state";
        String payload = "driving";

        Message<String> message = createMessage(topic, payload);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(newCar);
    }

    private Message<String> createMessage(String topic, String payload) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(TOPIC_HEADER, topic);
        return new GenericMessage<>(payload, headers);
    }

}