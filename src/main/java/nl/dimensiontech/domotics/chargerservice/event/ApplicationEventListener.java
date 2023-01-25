package nl.dimensiontech.domotics.chargerservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.message.service.SimpleMessageService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static nl.dimensiontech.domotics.chargerservice.constants.MqttConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventListener {

    private final SimpleMessageService messageService;

    @EventListener(ApplicationStartedEvent.class)
    void handleApplicationStartedEvent() {
        log.info("Application started, reporting status to broker.");
        messageService.sendMessage(STATUS_ONLINE, STATUS_TOPIC);
    }

    @EventListener(ContextClosedEvent.class)
    void handlePreDestroy() {
        log.info("Application shutting down, reporting status to broker.");
        messageService.sendMessage(STATUS_OFFLINE, STATUS_TOPIC);
    }

}
