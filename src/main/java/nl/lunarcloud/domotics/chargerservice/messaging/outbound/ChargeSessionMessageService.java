package nl.lunarcloud.domotics.chargerservice.messaging.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.api.model.ChargeSessionApi;
import nl.lunarcloud.domotics.chargerservice.common.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ChargeSessionMessageService extends AbstractMessageService<ChargeSessionApi> {

    @Autowired
    public ChargeSessionMessageService(OutboundMessageHandler messageHandler,
                                       ObjectMapper objectMapper,
                                       ConfigProperties configProperties) {

        super(messageHandler, objectMapper, configProperties);
    }

    @Override
    public void sendMessage(ChargeSessionApi ChargeSessionApi, String topic, boolean retain) {
        log.info("Publish session event to {}", topic);
        String payload = toJson(ChargeSessionApi);

        Map<String, Object> headers = Map.of(
                MqttHeaders.RETAINED, retain,
                MqttHeaders.TOPIC, topic
        );

        Message<String> message = new GenericMessage<>(payload, headers);

        messageHandler.handleMessage(message);
    }
}
