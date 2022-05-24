package nl.dimensiontech.domotics.chargerservice.scheduling;

import nl.dimensiontech.domotics.chargerservice.service.MailService;
import nl.dimensiontech.domotics.chargerservice.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulingServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private SchedulingService schedulingService;

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
        verify(mailService, times(1)).sendReminder();
        verifyNoMoreInteractions(mailService);
    }

}