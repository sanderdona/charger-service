package nl.dimensiontech.domotics.chargerservice.scheduling;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.service.ReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final ReportService reportService;

    @Scheduled(cron = "0 0 10 1 * *")
    public void generateMonthlyReport() {
        // get the correct month
        YearMonth lastMonth = YearMonth.from(ZonedDateTime.now().minusMonths(1));

        // get the first day of this month
        LocalDate startDate = lastMonth.atDay(1);

        // get the last day of this month
        LocalDate endDate = lastMonth.atEndOfMonth();

        reportService.generateReport(startDate, endDate);
    }

}
