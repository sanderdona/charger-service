package nl.dimensiontech.domotics.chargerservice.scheduling;

import nl.dimensiontech.domotics.chargerservice.service.MailService;
import nl.dimensiontech.domotics.chargerservice.reporting.ReportService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private MailService mailService;

    @Mock
    private Clock clock;

    @InjectMocks
    private SchedulingService schedulingService;

    @BeforeEach
    public void before() {
        when(clock.instant()).thenReturn(Instant.now());
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    public void shouldGenerateReportAndMail() throws Exception {
        // given
        File file = File.createTempFile("test", "pdf");
        when(reportService.generateReport(isA(LocalDate.class), isA(LocalDate.class))).thenReturn(Optional.of(file));

        // when
        schedulingService.generateMonthlyReport();

        // then
        verify(mailService, times(1)).sendGeneratedDeclaration(file);
        verifyNoMoreInteractions(mailService);
    }

    @Test
    public void shouldSendReminderOnAbsentReport() {
        // given
        when(reportService.generateReport(isA(LocalDate.class), isA(LocalDate.class))).thenReturn(Optional.empty());

        // when
        schedulingService.generateMonthlyReport();

        // then
        verify(mailService, times(1)).reportNotGenerated();
        verifyNoMoreInteractions(mailService);
    }

}