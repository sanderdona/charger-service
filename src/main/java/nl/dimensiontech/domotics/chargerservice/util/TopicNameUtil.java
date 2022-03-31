package nl.dimensiontech.domotics.chargerservice.util;

import org.springframework.messaging.Message;

import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.TOPIC_HEADER;
import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.TOPIC_SEPARATOR;

public class TopicNameUtil {

    public static Long getCarId(Message<?> message) {
        String topic = (String) message.getHeaders().get(TOPIC_HEADER);
        assert topic != null;

        String[] split = topic.split(TOPIC_SEPARATOR);
        return Long.valueOf(split[2]);
    }

    public static String getLastTopicName(Message<?> message) {
        String topic = (String) message.getHeaders().get(TOPIC_HEADER);
        assert topic != null;

        return topic.substring(topic.lastIndexOf("/") + 1);
    }

}
