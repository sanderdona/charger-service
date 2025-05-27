package nl.lunarcloud.domotics.chargerservice.cucumber;

import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.common.config.ConfigProperties;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
