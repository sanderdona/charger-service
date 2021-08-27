package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.service.ChargeSessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChargeSessionResource {

    private final ChargeSessionService chargeSessionService;

    @GetMapping(path = "charges")
    public List<ChargeSession> getCharges() {
        return chargeSessionService.getSessions();
    }
}
