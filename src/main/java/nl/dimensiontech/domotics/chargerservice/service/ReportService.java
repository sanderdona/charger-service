package nl.dimensiontech.domotics.chargerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import nl.dimensiontech.domotics.chargerservice.report.domain.ReportTable;
import nl.dimensiontech.domotics.chargerservice.report.PdfBuilder;
import nl.dimensiontech.domotics.chargerservice.report.domain.TableCell;
import nl.dimensiontech.domotics.chargerservice.report.domain.TableColumn;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static nl.dimensiontech.domotics.chargerservice.constants.PdfConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final static PDFont FONT_PLAIN = PDType1Font.HELVETICA;
    private final static PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;

    private final ChargeSessionService chargeSessionService;
    private final ConfigProperties configProperties;

    public void generateReport(LocalDate startDate, LocalDate endDate) {
        log.info("Generating report...");

        List<ChargeSession> chargeSessions = chargeSessionService.getSessionsInRange(startDate, endDate);
        List<ChargeSession> registeredChargeSessions = chargeSessions.stream()
                .filter(chargeSession -> ChargeSessionType.REGISTERED.equals(chargeSession.getChargeSessionType()))
                .collect(Collectors.toList());

        if (registeredChargeSessions.isEmpty()) {
            log.info("No charge sessions found for range {} to {}. Skipping report generation",
                    startDate, endDate);
            return;
        }

        float totalCharged = calculateTotalCharged(registeredChargeSessions);
        float tariff = configProperties.getTariff();
        String totalCosts = String.format("%.2f", totalCharged * tariff);
        String fileName = getFileName(startDate);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag(LANGUAGE_TAG));

        try {
            new PdfBuilder()
                    .createPage(PDRectangle.A4, DOCUMENT_MARGIN, TEXT_MARGIN)
                    .addTextLine(TEXT_TITLE, FONT_PLAIN, H1_FONT_SIZE)
                    .addTextLine(TEXT_ALGEMEEN, FONT_PLAIN, H2_FONT_SIZE)
                    .addLine()
                    .addTextLine(TEXT_KENTEKEN, FONT_PLAIN, P_FONT_SIZE)
                    .addTextLine(configProperties.getLicensePlate(), FONT_PLAIN, P_FONT_SIZE, 150, true)
                    .addTextLine(TEXT_PERIODE, FONT_PLAIN, P_FONT_SIZE)
                    .addTextLine(dateFormat.format(startDate), FONT_PLAIN, P_FONT_SIZE, 150, true)
                    .addTextLine(TEXT_LAADSESSIES, FONT_PLAIN, H2_FONT_SIZE)
                    .addLine()
                    .createTable()
                    .fromTable(createReportTable(chargeSessions).getTable())
                    .closeTable()
                    .addTextLine(TEXT_TOTAAL_KWH, FONT_BOLD, P_FONT_SIZE, MARGIN_LEFT_3_TABLES_SKIPPED, false)
                    .addTextLine(String.format("%.2f", totalCharged), FONT_PLAIN, P_FONT_SIZE, MARGIN_LEFT_4_TABLES_SKIPPED, true)
                    .addTextLine(TEXT_TARIEF_KWH, FONT_BOLD, P_FONT_SIZE, MARGIN_LEFT_3_TABLES_SKIPPED, false)
                    .addTextLine("\u20AC " + tariff, FONT_PLAIN, P_FONT_SIZE, MARGIN_LEFT_4_TABLES_SKIPPED, true)
                    .addTextLine(TEXT_TOTAAL, FONT_BOLD, P_FONT_SIZE + 1, MARGIN_LEFT_3_TABLES_SKIPPED, false)
                    .addTextLine("\u20AC " + totalCosts, FONT_BOLD, P_FONT_SIZE + 1, MARGIN_LEFT_4_TABLES_SKIPPED, true)
                    .closePage()
                    .save(fileName);

        } catch (IOException e) {
            log.error("Failed to create the report: {}", e.getMessage());
        }

        log.info("Report successfully generated!");
    }

    private String getFileName(LocalDate startDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        String period = startDate.format(dateFormat);
        return TEXT_DECLARATIE + "-" + period + FILE_EXTENSION;
    }

    private ReportTable createReportTable(List<ChargeSession> chargeSessions) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<TableColumn> tableColumns = new ArrayList<>();

        TableColumn gestartColumn = new TableColumn();
        gestartColumn.setName(TEXT_GESTART);
        gestartColumn.setWidth(TABLE_ROW_LARGE_WIDTH);
        tableColumns.add(gestartColumn);

        TableColumn voltooidColumn = new TableColumn();
        voltooidColumn.setName(TEXT_VOLTOOID);
        voltooidColumn.setWidth(TABLE_ROW_LARGE_WIDTH);
        tableColumns.add(voltooidColumn);

        TableColumn kmStandColumn = new TableColumn();
        kmStandColumn.setName(TEXT_KM_STAND);
        kmStandColumn.setWidth(TABLE_ROW_SMALL_WIDTH);
        tableColumns.add(kmStandColumn);

        TableColumn startKwhColumn = new TableColumn();
        startKwhColumn.setName(TEXT_START_KWH);
        startKwhColumn.setWidth(TABLE_ROW_MEDIUM_WIDTH);
        tableColumns.add(startKwhColumn);

        TableColumn eindKwhColumn = new TableColumn();
        eindKwhColumn.setName(TEXT_EIND_KWH);
        eindKwhColumn.setWidth(TABLE_ROW_MEDIUM_WIDTH);
        tableColumns.add(eindKwhColumn);

        TableColumn verbruiktKwhColumn = new TableColumn();
        verbruiktKwhColumn.setName(TEXT_VERBRUIKT);
        verbruiktKwhColumn.setWidth(TABLE_ROW_MEDIUM_WIDTH);
        tableColumns.add(verbruiktKwhColumn);

        ReportTable reportTable = new ReportTable();
        reportTable.getTableColumns().addAll(tableColumns);

        for (ChargeSession chargeSession : chargeSessions) {
            List<TableCell> cellValues = createCellValues(dateFormat, chargeSession);
            reportTable.createRow(cellValues);
        }

        return reportTable;
    }

    private List<TableCell> createCellValues(SimpleDateFormat dateFormat, ChargeSession chargeSession) {
        List<TableCell> cellValues = new ArrayList<>();

        Color fillColor;
        String totalCharged = "";
        if (ChargeSessionType.ANONYMOUS.equals(chargeSession.getChargeSessionType())) {
            fillColor = Color.LIGHT_GRAY;
        } else {
            fillColor = Color.WHITE;
            totalCharged = String.format("%.3f", chargeSession.getTotalkwH());
        }

        cellValues.add(new TableCell(dateFormat.format(chargeSession.getStartedAt()), fillColor));
        cellValues.add(new TableCell(dateFormat.format(chargeSession.getEndedAt()), fillColor));
        cellValues.add(new TableCell(String.valueOf(chargeSession.getOdoMeter()), fillColor));
        cellValues.add(new TableCell(String.format("%.3f", chargeSession.getStartkWh()), fillColor));
        cellValues.add(new TableCell(String.format("%.3f", chargeSession.getEndkWh()), fillColor));
        cellValues.add(new TableCell(totalCharged, fillColor));

        return cellValues;
    }

    private float calculateTotalCharged(List<ChargeSession> chargeSessions) {
        float total = 0f;

        for (ChargeSession chargeSession : chargeSessions) {
            total += chargeSession.getTotalkwH();
        }

        return total;
    }
}
