package nl.dimensiontech.domotics.chargerservice.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChargeSessionMessageService extends AbstractMessageService<ChargeSessionDto> {

    @Autowired
    public ChargeSessionMessageService(OutboundMessageHandler messageHandler, ObjectMapper objectMapper) {
        super(messageHandler, objectMapper);
    }

    @Override
    public void sendMessage(ChargeSessionDto chargeSessionDto) {
        String payload = toJson(chargeSessionDto);
        messageHandler.sendMessage(payload);
    }
}
