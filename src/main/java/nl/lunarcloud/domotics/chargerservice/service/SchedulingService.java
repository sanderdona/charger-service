package nl.lunarcloud.domotics.chargerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.report.ReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final ReportService reportService;
    private final MailService mailService;
    private final Clock clock;

    @Scheduled(cron = "0 0 10 1 * *")
    public void generateMonthlyReport() {
        log.info("Starting monthly cron job");

        YearMonth lastMonth = YearMonth.from(LocalDate.now(clock).minusMonths(1));
        LocalDate startDate = lastMonth.atDay(1);
        LocalDate endDate = lastMonth.atEndOfMonth();

        Optional<File> generatedReport = reportService.generateReport(startDate, endDate);
        generatedReport.ifPresentOrElse(mailService::sendGeneratedDeclaration, mailService::reportNotGenerated);

        log.info("Monthly cron job finished");
    }

}
