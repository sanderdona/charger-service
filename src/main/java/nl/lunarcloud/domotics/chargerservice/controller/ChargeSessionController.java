package nl.lunarcloud.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.lunarcloud.domotics.chargerservice.api.ChargeSessionsApi;
import nl.lunarcloud.domotics.chargerservice.api.model.ChargeSessionApi;
import nl.lunarcloud.domotics.chargerservice.api.model.ChargeSessionPageApi;
import nl.lunarcloud.domotics.chargerservice.common.exception.RecordNotFoundException;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;
import nl.lunarcloud.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.lunarcloud.domotics.chargerservice.service.ChargeSessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChargeSessionController implements ChargeSessionsApi {

    private final ChargeSessionService chargeSessionService;
    private final ChargeSessionMapper chargeSessionMapper;

    @Override
    public ResponseEntity<ChargeSessionPageApi> getChargeSessions(Integer page, Integer size, List<String> sort, Pageable pageable) {
        Page<ChargeSession> sessions = chargeSessionService.getSessions(pageable);
        return ResponseEntity.ok(chargeSessionMapper.map(sessions));
    }

    @Override
    public ResponseEntity<ChargeSessionApi> getChargeSession(String chargeSessionId) {
        ChargeSession chargeSession = chargeSessionService.getSessionById(UUID.fromString(chargeSessionId)).orElseThrow(
                () -> new RecordNotFoundException("ChargeSession with id " + chargeSessionId + " not found")
        );
        return ResponseEntity.ok(chargeSessionMapper.map(chargeSession));
    }
}
