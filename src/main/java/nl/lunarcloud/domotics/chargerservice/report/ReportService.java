package nl.lunarcloud.domotics.chargerservice.report;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.common.exception.ReportGenerationFailedException;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;
import nl.lunarcloud.domotics.chargerservice.service.ChargeSessionService;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSessionType;
import nl.lunarcloud.domotics.chargerservice.common.config.ConfigProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.lang.String.format;
import static nl.lunarcloud.domotics.chargerservice.report.ReportConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.forLanguageTag(LANGUAGE_TAG));
    private static final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag(LANGUAGE_TAG));
    private static final DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM", Locale.forLanguageTag(LANGUAGE_TAG));

    private final ChargeSessionService chargeSessionService;
    private final ConfigProperties configProperties;

    public Optional<File> generateReport(LocalDate startDate, LocalDate endDate) {
        log.info("Generating report...");

        List<ChargeSession> chargeSessions = chargeSessionService.getSessionsInRange(startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX), true);
        List<ChargeSession> registeredChargeSessions = chargeSessions.stream()
                .filter(chargeSession -> ChargeSessionType.REGISTERED.equals(chargeSession.getChargeSessionType()))
                .toList();

        if (registeredChargeSessions.isEmpty()) {
            log.warn("No charge sessions found for range {} to {}. Skipping report generation", startDate, endDate);
            return Optional.empty();
        }

        BigDecimal totalCharged = calculateTotalCharged(registeredChargeSessions);
        BigDecimal tariff = configProperties.getTariff();
        String totalCosts = format("%.2f", totalCharged.multiply(tariff));

        List<ReportRow> reportRows = chargeSessions.stream()
                .map((session) -> new ReportRow(
                        dateTimeFormatter.format(session.getStartedAt()),
                        dateTimeFormatter.format(session.getEndedAt()),
                        getOdoMeter(session),
                        format("%.2f", session.getStartKwh()),
                        format("%.2f", session.getEndKwh()),
                        format("%.2f", getTotalCharged(session)),
                        session.getChargeSessionType() == ChargeSessionType.ANONYMOUS
                ))
                .toList();

        Report report = new Report(
                configProperties.getLicensePlate(),
                monthYearFormatter.format(startDate),
                reportRows,
                format("%.2f", totalCharged),
                format("%.2f", tariff),
                totalCosts
        );

        String fileName = getFileName(startDate);
        return Optional.of(createFile(fileName, report));
    }

    private static String getOdoMeter(ChargeSession session) {
        if (session.getOdoMeter() == null) {
            return "";
        }
        return String.valueOf(session.getOdoMeter());
    }

    private String getFileName(LocalDate startDate) {
        String period = startDate.format(fileNameFormatter);
        return TEXT_DECLARATIE + "-" + period + FILE_EXTENSION;
    }

    private BigDecimal calculateTotalCharged(List<ChargeSession> chargeSessions) {
        BigDecimal total = BigDecimal.ZERO;

        for (ChargeSession chargeSession : chargeSessions) {
            total = total.add(getTotalCharged(chargeSession));
        }

        return total;
    }

    private BigDecimal getTotalCharged(ChargeSession chargeSession) {
        BigDecimal startKwh = BigDecimal.valueOf(chargeSession.getStartKwh());
        BigDecimal endKwh = BigDecimal.valueOf(chargeSession.getEndKwh());
        return endKwh.subtract(startKwh);
    }

    private File createFile(String fileName, Report report) {

        try {
            var mustacheFactory = new DefaultMustacheFactory();
            Mustache mustache = mustacheFactory.compile("report/report.mustache");
            StringWriter writer = new StringWriter();
            mustache.execute(writer, report).flush();

            Document document = Jsoup.parse(writer.toString(), "UTF-8");
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            Path filePath = Paths.get(fileName);
            Files.deleteIfExists(filePath);
            File pdfFile = filePath.toFile();

            try (OutputStream outputStream = new FileOutputStream(pdfFile)) {
                ITextRenderer renderer = new ITextRenderer();
                SharedContext sharedContext = renderer.getSharedContext();
                sharedContext.setPrint(true);
                sharedContext.setInteractive(false);
                renderer.setDocumentFromString(document.html());
                renderer.layout();
                renderer.createPDF(outputStream);
                log.info("Generated {}", pdfFile.getName());
            }

            return pdfFile;

        } catch (Exception exception) {
            log.error("Report generation failed!");
            throw new ReportGenerationFailedException(exception.getMessage());
        }
    }
}
