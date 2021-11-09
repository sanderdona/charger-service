package nl.dimensiontech.domotics.chargerservice.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.service.MailService;
import nl.dimensiontech.domotics.chargerservice.service.ReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingService {

    private final ReportService reportService;
    private final MailService mailService;

    @Scheduled(cron = "0 0 10 1 * *")
    public void generateMonthlyReport() {
        log.info("Starting monthly cron job");

        YearMonth lastMonth = YearMonth.from(LocalDate.now().minusMonths(1));
        LocalDate startDate = lastMonth.atDay(1);
        LocalDate endDate = lastMonth.atEndOfMonth();

        Optional<File> generatedReport = reportService.generateReport(startDate, endDate);
        generatedReport.ifPresent(mailService::sendEmail);

        log.info("Monthly cron job finished");
    }

}
