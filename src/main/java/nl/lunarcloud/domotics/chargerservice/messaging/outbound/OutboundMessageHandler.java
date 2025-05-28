package nl.lunarcloud.domotics.chargerservice.messaging.outbound;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway(defaultRequestChannel = "chargerMessageOutboundChannel")
public interface OutboundMessageHandler {

    void handleMessage(Message<?> message);
}
