package nl.dimensiontech.domotics.chargerservice.util;

import org.springframework.messaging.Message;

public class TopicNameUtil {

    private static final String TOPIC_SEPARATOR = "/";
    private static final String TOPIC_HEADER = "mqtt_receivedTopic";

    public static Long getCarId(Message<?> message) {
        String topic = (String) message.getHeaders().get(TOPIC_HEADER);
        assert topic != null;

        String[] split = topic.split(TOPIC_SEPARATOR);
        return Long.valueOf(split[2]);
    }

    public static String getValueName(Message<?> message) {
        String topic = (String) message.getHeaders().get(TOPIC_HEADER);
        assert topic != null;

        return topic.substring(topic.lastIndexOf("/") + 1);
    }

}
