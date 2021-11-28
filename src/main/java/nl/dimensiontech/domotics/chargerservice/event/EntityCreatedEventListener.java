package nl.dimensiontech.domotics.chargerservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.domain.Proof;
import nl.dimensiontech.domotics.chargerservice.service.MailService;
import nl.dimensiontech.domotics.chargerservice.service.ReportService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityCreatedEventListener {

    private final ReportService reportService;
    private final MailService mailService;

    @EventListener
    void handleProofCreatedEvent(EntityCreatedEvent<Proof> event) {
        Proof proof = event.getEntity();

        LocalDate currentDate = LocalDate.now();
        LocalDate proofDate = proof.getDate();

        if (currentDate.isAfter(proofDate)) {
            YearMonth previousMonth = YearMonth.from(proofDate.minusMonths(1));
            LocalDate startDate = previousMonth.atDay(1);
            LocalDate endDate = previousMonth.atEndOfMonth();

            log.info("Submitted a proof before the current date: generating report for range {} to {}", startDate, endDate);

            Optional<File> generatedReport = reportService.generateReport(startDate, endDate);
            generatedReport.ifPresent(mailService::sendEmail);
        }
    }
}
