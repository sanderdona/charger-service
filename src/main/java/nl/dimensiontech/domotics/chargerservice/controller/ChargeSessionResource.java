package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.dimensiontech.domotics.chargerservice.service.ChargeSessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("charges")
@RequiredArgsConstructor
public class ChargeSessionResource {

    private final ChargeSessionService chargeSessionService;
    private final ChargeSessionMapper mapper;

    @GetMapping
    @PreAuthorize("hasRole('client_read')")
    public Page<ChargeSessionDto> getCharges(@PageableDefault(sort = "startedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return chargeSessionService.getSessions(pageable).map(mapper::toDto);
    }
}
