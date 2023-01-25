package nl.dimensiontech.domotics.chargerservice.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SimpleMessageService extends AbstractMessageService<String> {

    @Autowired
    public SimpleMessageService(OutboundMessageHandler messageHandler,
                                ObjectMapper objectMapper,
                                ConfigProperties configProperties) {

        super(messageHandler, objectMapper, configProperties);
    }

    @Override
    public void sendMessage(String payload, String topic, boolean retain) {
        Map<String, Object> headers = Map.of(
                MqttHeaders.RETAINED, retain,
                MqttHeaders.TOPIC, topic
        );

        Message<String> message = new GenericMessage<>(payload, headers);

        messageHandler.handleMessage(message);
    }
}
