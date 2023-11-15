package nl.dimensiontech.domotics.chargerservice.repository.listener;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.message.model.ChargeSessionMessage;
import nl.dimensiontech.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.dimensiontech.domotics.chargerservice.message.service.ChargeSessionMessageService;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

@RequiredArgsConstructor
public class ChargeSessionEntityListener {

    private final ChargeSessionMessageService messageService;
    private final ChargeSessionMapper chargeSessionMapper;

    @PostPersist
    @PostUpdate
    private void afterPersistAndUpdate(ChargeSession chargeSession) {
        ChargeSessionMessage chargeSessionMessage = chargeSessionMapper.toMessage(chargeSession);
        messageService.sendMessage(chargeSessionMessage, true);
    }

}
