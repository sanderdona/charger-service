package nl.dimensiontech.domotics.chargerservice.service;

import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.repository.ChargeSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargeSessionServiceTest {

    @Mock
    private EnergyMeterService energyMeterService;

    @Mock
    private ChargeSessionRepository repository;

    @Captor
    private ArgumentCaptor<ChargeSession> chargeSessionCaptor;

    @InjectMocks
    private ChargeSessionService service;

    @Test
    public void shouldStartChargeSession() {
        // given
        final float chargePower = 9.578f;
        final float currentReading = 120.055f;
        when(repository.findByEndedAtIsNull()).thenReturn(Optional.empty());
        when(energyMeterService.getCurrentReading()).thenReturn(currentReading);

        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setId(1L);
        when(repository.save(isA(ChargeSession.class))).thenReturn(chargeSession);

        // when
        service.handleChargePowerUpdate(chargePower);

        // then
        verify(repository, times(1)).save(chargeSessionCaptor.capture());
        ChargeSession capturedSession = chargeSessionCaptor.getValue();

        assertThat(capturedSession.getStartkWh()).isEqualTo(currentReading);
    }

}