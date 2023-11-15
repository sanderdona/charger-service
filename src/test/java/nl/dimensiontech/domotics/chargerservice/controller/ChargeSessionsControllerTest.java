package nl.dimensiontech.domotics.chargerservice.controller;

import nl.dimensiontech.domotics.chargerservice.api.model.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.api.model.ChargeSessionPageDto;
import nl.dimensiontech.domotics.chargerservice.api.model.PageableDto;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.mapper.ChargeSessionMapper;
import nl.dimensiontech.domotics.chargerservice.service.ChargeSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargeSessionsControllerTest {

    @Mock
    private ChargeSessionService chargeSessionService;

    @Mock
    private ChargeSessionMapper mapper;

    @InjectMocks
    private ChargeSessionsController chargeSessionsController;

    @Test
    public void testGetChargeSessions() {
        // given
        ChargeSession chargeSession = new ChargeSession();
        when(chargeSessionService.getSessions(isA(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(chargeSession)));
        when(mapper.toDto(chargeSession)).thenReturn(new ChargeSessionDto());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setPageSize(1);

        // when
        ResponseEntity<ChargeSessionPageDto> response = chargeSessionsController.getChargeSessions(pageableDto);

        // then
        assertThat(response.getBody().getTotalPages()).isEqualTo(1);
    }

}