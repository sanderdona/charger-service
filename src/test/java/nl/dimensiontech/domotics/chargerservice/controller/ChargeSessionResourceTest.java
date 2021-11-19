package nl.dimensiontech.domotics.chargerservice.controller;

import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
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

    @InjectMocks
    private ChargeSessionResource chargeSessionResource;

    @Test
    public void testGetChargeSessions() {
        // given
        when(chargeSessionService.getSessions()).thenReturn(List.of(new ChargeSession()));

        // when
        List<ChargeSession> chargeSessions = chargeSessionResource.getCharges();

        // then
        assertThat(chargeSessions.size()).isEqualTo(1);
    }

}