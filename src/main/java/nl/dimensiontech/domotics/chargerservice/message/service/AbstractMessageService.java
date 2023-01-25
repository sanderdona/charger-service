package nl.dimensiontech.domotics.chargerservice.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;

import static org.eclipse.paho.client.mqttv3.MqttTopic.TOPIC_LEVEL_SEPARATOR;

public abstract class AbstractMessageService<T> implements MessageService<T> {

    protected OutboundMessageHandler messageHandler;
    protected ObjectMapper objectMapper;
    protected ConfigProperties configProperties;

    public AbstractMessageService(OutboundMessageHandler messageHandler, ObjectMapper objectMapper, ConfigProperties configProperties) {
        this.messageHandler = messageHandler;
        this.objectMapper = objectMapper;
        this.configProperties = configProperties;
    }

    @Override
    public void sendMessage(T payload) {
        var rootTopic = configProperties.getMqttConfig().getRootTopic();
        sendMessage(payload, rootTopic);
    }

    public void sendMessage(T payload, String topic) {
        var rootTopic = configProperties.getMqttConfig().getRootTopic();
        sendMessage(payload, String.join(TOPIC_LEVEL_SEPARATOR, rootTopic, topic), false);
    }

    protected String toJson(T t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map to JSON string:\n" + e.getMessage());
        }
    }
}
