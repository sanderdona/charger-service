package nl.dimensiontech.domotics.chargerservice.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChargeSessionMessageService extends AbstractMessageService<ChargeSession> {

    @Autowired
    public ChargeSessionMessageService(OutboundMessageHandler messageHandler, ObjectMapper objectMapper) {
        super(messageHandler, objectMapper);
    }

    @Override
    public void sendMessage(ChargeSession chargeSession) {
        String payload = toJson(chargeSession);
        messageHandler.sendMessage(payload);
    }
}
