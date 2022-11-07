package nl.dimensiontech.domotics.chargerservice.message.handler;

import nl.dimensiontech.domotics.chargerservice.service.ChargeSessionService;
import nl.dimensiontech.domotics.chargerservice.service.EnergyMeterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.TOPIC_HEADER;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargerMessageHandlerTest {

    @Mock
    private ChargeSessionService chargeSessionService;

    @Mock
    private EnergyMeterService energyMeterService;

    @InjectMocks
    private ChargerMessageHandler chargerMessageHandler;

    @Test
    public void shouldHandlePowerMessage() {
        // given
        String topic = "home/charger/sdm1-1/Power";
        String payload = "9.389";

        Message<String> message = createMessage(topic, payload);

        // when
        chargerMessageHandler.handleMessage(message);

        // then
        verify(chargeSessionService, times(1)).handleChargePowerUpdate(9.389f);
    }

    @Test
    public void shouldHandleEnergyImportMessage() {
        // given
        String topic = "home/charger/sdm1-1/Import";
        String payload = "1105.469";

        Message<String> message = createMessage(topic, payload);

        // when
        chargerMessageHandler.handleMessage(message);

        // then
        verify(energyMeterService, times(1)).setCurrentReading(1105.469d);
    }

    @Test
    public void shouldNotHandleUnknownTopic() {
        // given
        String topic = "home/charger/sdm1-1/Foo";
        String payload = "Bar";

        Message<String> message = createMessage(topic, payload);

        // when
        chargerMessageHandler.handleMessage(message);

        // then
        verifyNoInteractions(chargeSessionService);
        verifyNoInteractions(energyMeterService);
    }

    private Message<String> createMessage(String topic, String payload) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(TOPIC_HEADER, topic);
        return new GenericMessage<>(payload, headers);
    }

}