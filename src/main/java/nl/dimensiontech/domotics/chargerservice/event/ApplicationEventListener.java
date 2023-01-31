package nl.dimensiontech.domotics.chargerservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.message.service.SimpleMessageService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.STATUS_TOPIC;
import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.STATUS_ONLINE;
import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.STATUS_OFFLINE;
import static nl.dimensiontech.domotics.chargerservice.util.TopicNameUtil.transformToTopic;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventListener {

    private final ConfigProperties configProperties;

    private final SimpleMessageService messageService;

    @EventListener(ApplicationStartedEvent.class)
    void handleApplicationStartedEvent() {
        var statusTopic = getStatusTopic();
        log.info("Application started, reporting status on topic {}.", statusTopic);
        messageService.sendMessage(STATUS_ONLINE, statusTopic, true);
    }

    @EventListener(ContextClosedEvent.class)
    void handlePreDestroy() {
        log.info("Application shutting down, reporting status to broker.");
        messageService.sendMessage(STATUS_OFFLINE, getStatusTopic(), true);
    }

    private String getStatusTopic() {
        return transformToTopic(configProperties.getMqttConfig().getRootTopic(), STATUS_TOPIC);
    }

}
