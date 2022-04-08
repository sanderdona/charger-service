package nl.dimensiontech.domotics.chargerservice.message.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;

public abstract class AbstractMessageService<T> implements MessageService<T> {

    protected OutboundMessageHandler messageHandler;
    protected ObjectMapper objectMapper;

    public AbstractMessageService(OutboundMessageHandler messageHandler, ObjectMapper objectMapper) {
        this.messageHandler = messageHandler;
        this.objectMapper = objectMapper;
    }

    protected String toJson(T t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map to JSON string");
        }
    }
}
