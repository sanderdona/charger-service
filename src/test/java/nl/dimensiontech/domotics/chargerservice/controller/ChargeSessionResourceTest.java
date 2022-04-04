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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
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
        ChargeSession chargeSession = new ChargeSession();
        when(chargeSessionService.getSessions(isA(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(chargeSession)));
        when(mapper.toDto(chargeSession)).thenReturn(new ChargeSessionDto());

        // when
        Page<ChargeSessionDto> chargeSessionPage = chargeSessionResource.getCharges(Pageable.ofSize(1));

        // then
        assertThat(chargeSessionPage.getTotalPages()).isEqualTo(1);
    }

}