package nl.dimensiontech.domotics.chargerservice.service;

import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import nl.dimensiontech.domotics.chargerservice.repository.ChargeSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargeSessionServiceTest {

    @Mock
    private EnergyMeterService energyMeterService;

    @Mock
    private ChargeSessionRepository chargeSessionRepository;

    @Captor
    private ArgumentCaptor<ChargeSession> chargeSessionCaptor;

    @InjectMocks
    private ChargeSessionService chargeSessionService;

    @Test
    public void testStartChargeSession() {
        // given
        final float chargePower = 9.578f;
        final float currentReading = 120.055f;
        when(chargeSessionRepository.findByEndedAtIsNull()).thenReturn(Optional.empty());
        when(energyMeterService.getCurrentReading()).thenReturn(currentReading);

        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setId(1L);
        when(chargeSessionRepository.save(isA(ChargeSession.class))).thenReturn(chargeSession);

        // when
        chargeSessionService.handleChargePowerUpdate(chargePower);

        // then
        verify(chargeSessionRepository, times(1)).save(chargeSessionCaptor.capture());
        ChargeSession capturedSession = chargeSessionCaptor.getValue();

        assertThat(capturedSession.getStartkWh()).isEqualTo(currentReading);
        assertThat(capturedSession.getChargeSessionType()).isEqualTo(ChargeSessionType.ANONYMOUS);
    }

    @Test
    public void testEndActiveSession() {
        // given
        final float chargePower = 0f;
        final float startkWh = 120.055f;
        final float currentReading = 161.013f;
        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setId(1L);
        chargeSession.setStartkWh(startkWh);
        when(chargeSessionRepository.findByEndedAtIsNull()).thenReturn(Optional.of(chargeSession));
        when(energyMeterService.getCurrentReading()).thenReturn(currentReading);

        // when
        chargeSessionService.handleChargePowerUpdate(chargePower);

        // then
        verify(chargeSessionRepository, times(1)).save(chargeSessionCaptor.capture());
        ChargeSession capturedSession = chargeSessionCaptor.getValue();

        assertThat(capturedSession.getStartkWh()).isEqualTo(startkWh);
        assertThat(capturedSession.getEndkWh()).isEqualTo(currentReading);
        assertThat(capturedSession.getTotalkwH()).isEqualTo(currentReading - startkWh);
    }

    @Test
    public void testNoInteractionsOnChargePowerAndActiveSession() {
        // given
        final float chargePower = 9.578f;
        ChargeSession chargeSession = new ChargeSession();
        when(chargeSessionRepository.findByEndedAtIsNull()).thenReturn(Optional.of(chargeSession));

        // when
        chargeSessionService.handleChargePowerUpdate(chargePower);

        // then
        verify(chargeSessionRepository, never()).save(isA(ChargeSession.class));
        verifyNoInteractions(energyMeterService);
    }

    @Test
    public void testNoInteractionsOnNoChargePowerAndNoActiveSession() {
        // given
        final float chargePower = 0f;
        when(chargeSessionRepository.findByEndedAtIsNull()).thenReturn(Optional.empty());

        // when
        chargeSessionService.handleChargePowerUpdate(chargePower);

        // then
        verify(chargeSessionRepository, never()).save(isA(ChargeSession.class));
        verifyNoInteractions(energyMeterService);
    }

    @Test
    public void testAssignToActiveSession() {
        // given
        Car car = new Car();
        car.setName("foo");
        car.setOdometer(16504);
        ChargeSession chargeSession = new ChargeSession();
        when(chargeSessionRepository.findByEndedAtIsNull()).thenReturn(Optional.of(chargeSession));

        // when
        boolean assigned = chargeSessionService.assignToActiveSession(car);

        // then
        assertThat(assigned).isTrue();

        verify(chargeSessionRepository, times(1)).save(chargeSessionCaptor.capture());
        ChargeSession capturedSession = chargeSessionCaptor.getValue();

        assertThat(capturedSession.getCar()).isEqualTo(car);
        assertThat(capturedSession.getOdoMeter()).isEqualTo(car.getOdometer());
        assertThat(capturedSession.getChargeSessionType()).isEqualTo(ChargeSessionType.REGISTERED);
    }

    @Test
    public void testAssignToActiveSessionNoSessionActive() {
        // given
        Car car = new Car();
        when(chargeSessionRepository.findByEndedAtIsNull()).thenReturn(Optional.empty());

        // when
        boolean assigned = chargeSessionService.assignToActiveSession(car);

        // then
        assertThat(assigned).isFalse();
        verify(chargeSessionRepository, never()).save(isA(ChargeSession.class));
    }

    @Test
    public void testGetSessionsInRange() {
        // given
        LocalDate startDate = LocalDate.of(2021, Month.OCTOBER, 1);
        LocalDate endDate = LocalDate.of(2021, Month.OCTOBER, 31);
        List<ChargeSession> chargeSessions = createChargeSessionList();

        when(chargeSessionRepository.findAllByEndedAtBetweenOrderByIdAsc(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                .thenReturn(chargeSessions);

        // when
        List<ChargeSession> sessionsInRange = chargeSessionService.getSessionsInRange(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX));

        // then
        assertThat(sessionsInRange).hasSize(4);
    }

    @Test
    public void testGetSessionsInRangeWithoutZeroUsage() {
        // given
        LocalDate startDate = LocalDate.of(2021, Month.OCTOBER, 1);
        LocalDate endDate = LocalDate.of(2021, Month.OCTOBER, 31);
        List<ChargeSession> chargeSessions = createChargeSessionList();

        when(chargeSessionRepository.findAllByEndedAtBetweenOrderByIdAsc(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                .thenReturn(chargeSessions);

        // when
        List<ChargeSession> sessionsInRange = chargeSessionService.getSessionsInRange(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                true);

        // then
        assertThat(sessionsInRange).hasSize(3);
    }

    @Test
    public void testGetPageOfSessions() {
        // given
        Pageable pageable = Pageable.ofSize(1);
        PageImpl<ChargeSession> chargeSessionPage = new PageImpl<>(Collections.singletonList(new ChargeSession()));
        when(chargeSessionRepository.findAll(pageable)).thenReturn(chargeSessionPage);

        // when
        Page<ChargeSession> chargeSessionPageResult = chargeSessionService.getSessions(pageable);

        // then
        assertThat(chargeSessionPageResult).isEqualTo(chargeSessionPage);
    }

    private List<ChargeSession> createChargeSessionList() {
        return Arrays.asList(
                createChargeSession(45.556f, 46.732f),
                createChargeSession(46.732f, 46.732f),
                createChargeSession(46.732f, 51.184f),
                createChargeSession(51.184f, 67.284f)
        );
    }

    private ChargeSession createChargeSession(float startkWh, float endkWh) {
        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setStartkWh(startkWh);
        chargeSession.setEndkWh(endkWh);
        return chargeSession;
    }

}