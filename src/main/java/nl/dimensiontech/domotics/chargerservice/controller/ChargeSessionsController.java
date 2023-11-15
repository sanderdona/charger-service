package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.api.ChargeSessionsApi;
import nl.dimensiontech.domotics.chargerservice.api.model.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.api.model.ChargeSessionPageDto;
import nl.dimensiontech.domotics.chargerservice.api.model.PageableDto;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.dimensiontech.domotics.chargerservice.mapper.PageMapper;
import nl.dimensiontech.domotics.chargerservice.service.ChargeSessionService;
import nl.dimensiontech.domotics.chargerservice.util.UUIDUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChargeSessionsController implements ChargeSessionsApi {

    private final ChargeSessionService chargeSessionService;
    private final ChargeSessionMapper chargeSessionMapper;
    private final PageMapper pageMapper;

    @Override
    @Secured("client_read")
    public ResponseEntity<ChargeSessionDto> getChargeSession(String chargeSessionId) {
        var uuid = UUIDUtil.toUUID(chargeSessionId);
        return ResponseEntity.ok(chargeSessionMapper.toDto(chargeSessionService.getSession(uuid)));
    }

    @Override
    @Secured("client_read")
    public ResponseEntity<ChargeSessionPageDto> getChargeSessions(PageableDto pageable) {
        Page<ChargeSession> chargeSessionPage = chargeSessionService.getSessions(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())
        );
        return ResponseEntity.ok(pageMapper.toChargeSessionPageDto(chargeSessionPage));
    }
}
