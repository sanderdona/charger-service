package nl.dimensiontech.domotics.chargerservice.service;

import com.google.common.io.Files;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import nl.dimensiontech.domotics.chargerservice.reporting.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.dimensiontech.domotics.chargerservice.constants.PdfConstants.FILE_EXTENSION;
import static nl.dimensiontech.domotics.chargerservice.constants.PdfConstants.TEXT_DECLARATIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ChargeSessionService chargeSessionService;

    @Mock
    private ConfigProperties configProperties;

    @Mock
    private ProofService proofService;

    @Mock
    private Clock clock;

    @InjectMocks
    private ReportService reportService;

    @Test
    public void testGenerateReport() throws IOException {
        // given
        List<ChargeSession> chargeSessions = new ArrayList<>();

        createChargeSession(chargeSessions, 1L, ChargeSessionType.REGISTERED,
                LocalDateTime.of(2021, Month.OCTOBER, 1, 12, 17),
                LocalDateTime.of(2021, Month.OCTOBER, 1, 17, 7),
                15954, 1147.427d, 1191.307d);

        createChargeSession(chargeSessions, 2L, ChargeSessionType.ANONYMOUS,
                LocalDateTime.of(2021, Month.OCTOBER, 2, 8, 41),
                LocalDateTime.of(2021, Month.OCTOBER, 2, 8, 52),
                0, 1191.307d, 1193.022d);

        createChargeSession(chargeSessions, 3L, ChargeSessionType.ANONYMOUS,
                LocalDateTime.of(2021, Month.OCTOBER, 2, 9, 6),
                LocalDateTime.of(2021, Month.OCTOBER, 2, 10, 28),
                0, 1193.022d, 1204.621d);

        createChargeSession(chargeSessions, 4L, ChargeSessionType.REGISTERED,
                LocalDateTime.of(2021, Month.OCTOBER, 7, 21, 38),
                LocalDateTime.of(2021, Month.OCTOBER, 8, 1, 14),
                16100, 1204.621d, 1236.785d);

        createChargeSession(chargeSessions, 5L, ChargeSessionType.ANONYMOUS,
                LocalDateTime.of(2021, Month.OCTOBER, 8, 7, 40),
                LocalDateTime.of(2021, Month.OCTOBER, 8, 7, 59),
                0, 1236.785d, 1238.878d);

        createChargeSession(chargeSessions, 6L, ChargeSessionType.REGISTERED,
                LocalDateTime.of(2021, Month.OCTOBER, 18, 17, 6),
                LocalDateTime.of(2021, Month.OCTOBER, 18, 17, 6),
                16269, 1238.878d, 1238.889d);

        createChargeSession(chargeSessions, 7L, ChargeSessionType.REGISTERED,
                LocalDateTime.of(2021, Month.OCTOBER, 18, 17, 26),
                LocalDateTime.of(2021, Month.OCTOBER, 18, 21, 30),
                16269, 1238.889d, 1275.174d);

        LocalDate startDate = LocalDate.of(2021, Month.OCTOBER, 1);
        LocalDate endDate = startDate.plusMonths(1L);

        when(chargeSessionService.getSessionsInRange(isA(LocalDateTime.class), isA(LocalDateTime.class), eq(true)))
                .thenReturn(chargeSessions);

        ConfigProperties.ProofConfig proofConfig = new ConfigProperties.ProofConfig();
        proofConfig.setProofsRequired(false);
        when(configProperties.getProofConfig()).thenReturn(proofConfig);
        when(configProperties.getLicensePlate()).thenReturn("AB-123-C");
        when(configProperties.getTariff()).thenReturn(new BigDecimal("0.22"));

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

    @Test
    public void testSkipGenerationWhenNoChargeSessionsAvailable() {
        // given
        LocalDate startDate = LocalDate.of(2021, Month.OCTOBER, 1);
        LocalDate endDate = startDate.plusMonths(1L);

        List<ChargeSession> chargeSessions = new ArrayList<>();

        createChargeSession(chargeSessions, 1L, ChargeSessionType.ANONYMOUS,
                LocalDateTime.of(2021, Month.OCTOBER, 1, 12, 17),
                LocalDateTime.of(2021, Month.OCTOBER, 1, 17, 7),
                0, 1147.427f, 1191.307f);

        createChargeSession(chargeSessions, 2L, ChargeSessionType.ANONYMOUS,
                LocalDateTime.of(2021, Month.OCTOBER, 2, 8, 41),
                LocalDateTime.of(2021, Month.OCTOBER, 2, 8, 52),
                0, 1191.307f, 1193.022f);

        when(chargeSessionService.getSessionsInRange(isA(LocalDateTime.class), isA(LocalDateTime.class), eq(true)))
                .thenReturn(chargeSessions);

        // when
        Optional<File> optionalFile = reportService.generateReport(startDate, endDate);

        // then
        assertThat(optionalFile).isEmpty();
    }

    private Proof createProof(String imageName) throws IOException {
        Proof startOfMonthProof = new Proof();
        String fileName = "src/test/resources/images/" + imageName;
        File file = new File(fileName);
        startOfMonthProof.setFile(Files.toByteArray(file));
        return startOfMonthProof;
    }

    private void createChargeSession(List<ChargeSession> chargeSessions,
                                     long id,
                                     ChargeSessionType type,
                                     LocalDateTime startedAt,
                                     LocalDateTime endedAt,
                                     int odoMeter,
                                     double startkWh,
                                     double endkWh) {

        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setId(id);
        chargeSession.setChargeSessionType(type);
        chargeSession.setStartedAt(startedAt);
        chargeSession.setEndedAt(endedAt);
        chargeSession.setOdoMeter(odoMeter);
        chargeSession.setStartkWh(startkWh);
        chargeSession.setEndkWh(endkWh);
        chargeSession.setTotalkwH(chargeSession.getEndkWh() - chargeSession.getStartkWh());

        chargeSessions.add(chargeSession);
    }

}