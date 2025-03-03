package nl.lunarcloud.domotics.chargerservice.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import nl.lunarcloud.domotics.chargerservice.api.model.ChargeSessionApi;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;
import nl.lunarcloud.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.lunarcloud.domotics.chargerservice.messaging.outbound.ChargeSessionMessageService;

@RequiredArgsConstructor
public class ChargeSessionEntityListener {

    private final ChargeSessionMessageService messageService;
    private final ChargeSessionMapper chargeSessionMapper;

    @PostPersist
    @PostUpdate
    private void afterPersistAndUpdate(ChargeSession chargeSession) {
        ChargeSessionApi chargeSessionDto = chargeSessionMapper.map(chargeSession);
        messageService.sendMessage(chargeSessionDto, true);
    }

}
