package nl.dimensiontech.domotics.chargerservice.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;
import org.springframework.stereotype.Component;

@Component
public class MqttSubscribedListener implements ApplicationListener<MqttSubscribedEvent> {

    @Override
    public void onApplicationEvent(MqttSubscribedEvent event) {
        System.out.println("Subscribed success: " + event.getMessage());
    }
}
