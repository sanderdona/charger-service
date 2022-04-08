package nl.dimensiontech.domotics.chargerservice.message.handler;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "chargerMessageOutboundChannel")
public interface OutboundMessageHandler {

    void sendMessage(String data);
}
