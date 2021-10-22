package nl.dimensiontech.domotics.chargerservice.service;

import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static nl.dimensiontech.domotics.chargerservice.constants.PdfConstants.FILE_EXTENSION;
import static nl.dimensiontech.domotics.chargerservice.constants.PdfConstants.TEXT_DECLARATIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ChargeSessionService chargeSessionService;

    @Mock
    private ConfigProperties configProperties;

    @InjectMocks
    private ReportService reportService;

    @Test
    public void testGenerateReport() {
        // given
        List<ChargeSession> chargeSessions = new ArrayList<>();

        createChargeSession(chargeSessions, 1L, ChargeSessionType.REGISTERED, 16035, 120.455f, 141.121f);
        createChargeSession(chargeSessions, 2L, ChargeSessionType.REGISTERED, 16368, 141.121f, 174.931f);
        createChargeSession(chargeSessions, 3L, ChargeSessionType.ANONYMOUS, 0, 174.931f, 184.194f);
        createChargeSession(chargeSessions, 4L, ChargeSessionType.REGISTERED, 16642, 184.194f, 216.633f);
        createChargeSession(chargeSessions, 5L, ChargeSessionType.REGISTERED, 16883, 216.633f, 240.257f);
        createChargeSession(chargeSessions, 6L, ChargeSessionType.REGISTERED, 17157, 240.257f, 263.110f);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(1L);

        when(chargeSessionService.getSessionsInRange(isA(LocalDateTime.class), isA(LocalDateTime.class)))
                .thenReturn(chargeSessions);
        when(configProperties.getTariff()).thenReturn(0.22f);
        when(configProperties.getLicensePlate()).thenReturn("AB-123-C");

        // when
        reportService.generateReport(startDate, endDate);

        // then
        String projectRoot = System.getProperty("user.dir");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        String period = startDate.format(dateFormat);

        File file = new File(projectRoot + "/" + TEXT_DECLARATIE + "-" + period + FILE_EXTENSION);

        // Not much of a test...
        assertThat(file.exists()).isTrue();
        assertThat(file.delete()).isTrue();
    }

    private void createChargeSession(List<ChargeSession> chargeSessions, long id, ChargeSessionType type, int odoMeter,
                                     float startkWh, float endkWh) {

        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setId(id);
        chargeSession.setChargeSessionType(type);
        chargeSession.setStartedAt(LocalDateTime.now());
        chargeSession.setEndedAt(LocalDateTime.now());
        chargeSession.setOdoMeter(odoMeter);
        chargeSession.setStartkWh(startkWh);
        chargeSession.setEndkWh(endkWh);
        chargeSession.setTotalkwH(chargeSession.getEndkWh() - chargeSession.getStartkWh());

        chargeSessions.add(chargeSession);
    }

}