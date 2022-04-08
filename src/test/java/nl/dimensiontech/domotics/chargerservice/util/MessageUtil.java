package nl.dimensiontech.domotics.chargerservice.util;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

public class MessageUtil {

    public static Message<String> createMessage(String topic, String payload) {
        MessageHeaders messageHeaders = new MessageHeaders(
                Map.of("mqtt_receivedTopic", topic)
        );
        return new GenericMessage<>(payload, messageHeaders);
    }

}
