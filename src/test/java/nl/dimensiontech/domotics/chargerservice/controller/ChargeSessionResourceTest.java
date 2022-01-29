package nl.dimensiontech.domotics.chargerservice.controller;

import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.dimensiontech.domotics.chargerservice.service.ChargeSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargeSessionResourceTest {

    @Mock
    private ChargeSessionService chargeSessionService;

    @Mock
    private ChargeSessionMapper mapper;

    @InjectMocks
    private ChargeSessionResource chargeSessionResource;

    @Test
    public void testGetChargeSessions() {
        // given
        List<ChargeSession> sessions = List.of(new ChargeSession());
        when(chargeSessionService.getSessions()).thenReturn(sessions);
        when(mapper.toDto(sessions)).thenReturn(List.of(new ChargeSessionDto()));

        // when
        List<ChargeSessionDto> chargeSessions = chargeSessionResource.getCharges();

        // then
        assertThat(chargeSessions.size()).isEqualTo(1);
    }

}