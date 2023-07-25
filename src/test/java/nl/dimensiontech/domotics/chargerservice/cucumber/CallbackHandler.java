package nl.dimensiontech.domotics.chargerservice.cucumber;

import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;

import java.util.*;

@Slf4j
public class CallbackHandler implements MqttCallback {

    protected static IMqttClient mqttClient;

    protected final static Map<String, LinkedList<MqttMessage>> receivedMessages = new HashMap<>();

    public CallbackHandler(ConfigProperties properties) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                return;
            }

            DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
            mqttClient = factory.getClientInstance(properties.getMqttConfig().getHost(), CallbackHandler.class.getName());
            mqttClient.connect();

            mqttClient.setCallback(this);
            mqttClient.subscribe("#");
        } catch (MqttException e) {
            log.error("Failed starting the MQTT test client");
        }
    }

    public MqttMessage getLastMessage(String topic) {
        if (receivedMessages.containsKey(topic)) {
            return receivedMessages.get(topic).getLast();
        }

        return null;
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.error("[CUCUMBER] Connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        if (receivedMessages.containsKey(topic)) {
            receivedMessages.get(topic).addLast(message);
        } else {
            receivedMessages.put(topic, new LinkedList<>(Collections.singletonList(message)));
        }

        log.info("[CUCUMBER] Message arrived on topic {}", topic);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // nothing to do
    }
}
