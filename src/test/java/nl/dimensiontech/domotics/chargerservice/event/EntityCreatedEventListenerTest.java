package nl.dimensiontech.domotics.chargerservice.event;

import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import nl.dimensiontech.domotics.chargerservice.service.MailService;
import nl.dimensiontech.domotics.chargerservice.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityCreatedEventListenerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private EntityCreatedEventListener eventListener;

    @Test
    public void testHandleMessage() throws IOException {
        // given
        YearMonth currentMonth = YearMonth.from(LocalDate.now());
        YearMonth previousMonth = YearMonth.from(LocalDate.now().minusMonths(1));
        LocalDate startOfPreviousMonth = previousMonth.atDay(1);
        LocalDate endOfPreviousMonth = previousMonth.atEndOfMonth();

        Proof proof = new Proof();
        proof.setDate(currentMonth.atDay(1));
        EntityCreatedEvent<Proof> event = mock(EntityCreatedEvent.class);
        when(event.getEntity()).thenReturn(proof);

        File tempFile = File.createTempFile("test", ".pdf");

        // get rid of the file when VM exits
        tempFile.deleteOnExit();

        when(reportService.generateReport(startOfPreviousMonth, endOfPreviousMonth))
                .thenReturn(Optional.of(tempFile));

        // when
        eventListener.handleProofCreatedEvent(event);

        // then
        verify(mailService, times(1)).sendGeneratedDeclaration(tempFile);
    }
}