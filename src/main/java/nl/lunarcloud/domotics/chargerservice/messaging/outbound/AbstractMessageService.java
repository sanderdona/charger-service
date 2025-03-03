package nl.lunarcloud.domotics.chargerservice.messaging.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.lunarcloud.domotics.chargerservice.common.config.ConfigProperties;

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
        sendMessage(payload, configProperties.getMqttConfig().getRootTopic());
    }

    @Override
    public void sendMessage(T payload, boolean retain) {
        sendMessage(payload, configProperties.getMqttConfig().getRootTopic(), retain);
    }

    @Override
    public void sendMessage(T payload, String topic) {
        sendMessage(payload, topic, false);
    }

    protected String toJson(T t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map to JSON string:\n" + e.getMessage());
        }
    }
}
