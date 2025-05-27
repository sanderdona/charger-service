package nl.lunarcloud.domotics.chargerservice.events;

import lombok.RequiredArgsConstructor;
import nl.lunarcloud.domotics.chargerservice.api.model.ChargeSessionApi;
import nl.lunarcloud.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.lunarcloud.domotics.chargerservice.messaging.outbound.ChargeSessionMessageService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChargeSessionEventListener {

    private final ChargeSessionMessageService messageService;
    private final ChargeSessionMapper chargeSessionMapper;

    @EventListener
    private void handleChargeSessionSaved(ChargeSessionSavedEvent event) {
        ChargeSessionApi chargeSessionDto = chargeSessionMapper.map(event.chargeSession());
        messageService.sendMessage(chargeSessionDto, true);
    }

}
