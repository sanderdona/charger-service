package nl.dimensiontech.domotics.chargerservice.listener;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.message.service.ChargeSessionMessageService;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

@RequiredArgsConstructor
public class ChargeSessionEntityListener {

    private final ChargeSessionMessageService chargeSessionMessageService;

    @PostPersist
    @PostUpdate
    private void afterPersistAndUpdate(ChargeSession chargeSession) {
        chargeSessionMessageService.sendMessage(chargeSession);
    }

}
