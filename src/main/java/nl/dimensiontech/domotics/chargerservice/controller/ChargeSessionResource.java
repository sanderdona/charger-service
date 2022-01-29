package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.dimensiontech.domotics.chargerservice.service.ChargeSessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChargeSessionResource {

    private final ChargeSessionService chargeSessionService;
    private final ChargeSessionMapper mapper;

    @GetMapping(path = "charges")
    public List<ChargeSessionDto> getCharges() {
        return mapper.toDto(chargeSessionService.getSessions());
    }
}
