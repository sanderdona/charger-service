package nl.lunarcloud.domotics.chargerservice.events;

import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;

public record ChargeSessionSavedEvent(ChargeSession chargeSession) {
}
