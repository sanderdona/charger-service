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
    private CarMessageHandler carMessageHandler;

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

        Map<String, Object> headers = new HashMap<>();
        headers.put(TOPIC_HEADER, topic);
        Message<String> message = new GenericMessage<>(payload, headers);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getCarState()).isEqualTo(CarState.DRIVING);
    }

    @Test
    public void shouldHandleUnknownCarState() {
        // given
        String topic = "teslamate/cars/1/state";
        String payload = "foo";

        Map<String, Object> headers = new HashMap<>();
        headers.put(TOPIC_HEADER, topic);
        Message<String> message = new GenericMessage<>(payload, headers);

        // when
        carMessageHandler.handleMessage(message);

        // then
        verify(carService, times(1)).handleStateChange(carCaptor.capture());

        Car capturedCar = carCaptor.getValue();
        assertThat(capturedCar.getCarState()).isEqualTo(CarState.ONLINE);
    }

}