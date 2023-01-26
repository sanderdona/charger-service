package nl.dimensiontech.domotics.chargerservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.messaging.Message;

import static org.eclipse.paho.client.mqttv3.MqttTopic.TOPIC_LEVEL_SEPARATOR;
import static org.springframework.integration.mqtt.support.MqttHeaders.RECEIVED_TOPIC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopicNameUtil {

    public static Long getCarId(Message<?> message) {
        String topic = getTopic(message);
        String[] split = topic.split(TOPIC_LEVEL_SEPARATOR);
        return Long.valueOf(split[2]);
    }

    public static String getLastTopicName(Message<?> message) {
        String topic = getTopic(message);
        return topic.substring(topic.lastIndexOf("/") + 1);
    }

    private static String getTopic(Message<?> message) {
        String topic = (String) message.getHeaders().get(RECEIVED_TOPIC);
        assert topic != null;
        return topic;
    }

}
