package nl.dimensiontech.domotics.chargerservice.listener;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.dimensiontech.domotics.chargerservice.message.service.ChargeSessionMessageService;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

@RequiredArgsConstructor
public class ChargeSessionEntityListener {

    private final ChargeSessionMessageService chargeSessionMessageService;
    private final ChargeSessionMapper chargeSessionMapper;

    @PostPersist
    @PostUpdate
    private void afterPersistAndUpdate(ChargeSession chargeSession) {
        ChargeSessionDto chargeSessionDto = chargeSessionMapper.toDto(chargeSession);
        chargeSessionMessageService.sendMessage(chargeSessionDto);
    }

}
