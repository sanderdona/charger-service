package nl.dimensiontech.domotics.chargerservice.service;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        String fileName = getFileName(startDate);
        saveAsPdf(fileName, chargeSessions, totalCharged, startDate);

        log.info("Report successfully generated!");
    }

    private String getFileName(LocalDate startDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        String period = startDate.format(dateFormat);
        return TEXT_DECLARATIE + "-" + period + FILE_EXTENSION;
    }

    private float calculateTotalCharged(List<ChargeSession> chargeSessions) {
        float total = 0f;

        for (ChargeSession chargeSession : chargeSessions) {
            total += chargeSession.getTotalkwH();
        }

        return total;
    }

    private void saveAsPdf(String fileName, List<ChargeSession> chargeSessions,
                           float totalCharged, LocalDate startDate) {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);

        try {
            PDPageContentStream content = new PDPageContentStream(document, page);
            float mediaBoxHeight = page.getMediaBox().getHeight();
            float contentAreaHeight = mediaBoxHeight - DOCUMENT_MARGIN;
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("nl-NL"));
            String period = dateFormat.format(startDate);

            newLine(content, H1_FONT_SIZE, contentAreaHeight, TEXT_TITLE);

            newLine(content, H2_FONT_SIZE, contentAreaHeight - 30, TEXT_ALGEMEEN);
            newLine(content, P_FONT_SIZE, contentAreaHeight - 60, TEXT_KENTEKEN);
            newLine(content, P_FONT_SIZE, 150, contentAreaHeight - 60, configProperties.getLicensePlate());
            newLine(content, P_FONT_SIZE, contentAreaHeight - 75, TEXT_PERIODE);
            newLine(content, P_FONT_SIZE, 150, contentAreaHeight - 75, period);

            newLine(content, H2_FONT_SIZE, contentAreaHeight - 120, TEXT_LAADSESSIES);

            BaseTable chargeTable = new BaseTable(contentAreaHeight - 150, 0,
                    0, DOCUMENT_CONTENT_WIDTH, DOCUMENT_MARGIN, document, page, true, true);
            createHeaderRow(chargeTable);
            for (ChargeSession chargeSession : chargeSessions) {
                createRow(chargeTable, chargeSession);
            }
            chargeTable.draw();

            float yStartAfterTable = contentAreaHeight - chargeTable.getHeaderAndDataHeight() - 180;

            float skippedRowsWidthForTitle = TABLE_ROW_LARGE_WIDTH + TABLE_ROW_LARGE_WIDTH + TABLE_ROW_SMALL_WIDTH +
                    TABLE_ROW_MEDIUM_WIDTH;
            float skippedRowsWidthForValue = skippedRowsWidthForTitle + TABLE_ROW_MEDIUM_WIDTH;
            float marginLeftTitle = (chargeTable.getWidth() * ((skippedRowsWidthForTitle) / 100)) + DOCUMENT_MARGIN;
            float marginLeftValue = (chargeTable.getWidth() * ((skippedRowsWidthForValue) / 100)) + DOCUMENT_MARGIN;

            newLine(content, FONT_BOLD, P_FONT_SIZE, marginLeftTitle, yStartAfterTable, TEXT_TOTAAL_KWH);
            newLine(content, P_FONT_SIZE, marginLeftValue, yStartAfterTable, String.format("%.2f", totalCharged));

            float tariff = configProperties.getTariff();

            newLine(content, FONT_BOLD, P_FONT_SIZE, marginLeftTitle, yStartAfterTable - 15, TEXT_TARIEF_KWH);
            newLine(content, P_FONT_SIZE, marginLeftValue, yStartAfterTable - 15, "\u20AC " + tariff);

            float separatorWidth = DOCUMENT_CONTENT_WIDTH * ((TABLE_ROW_MEDIUM_WIDTH + TABLE_ROW_MEDIUM_WIDTH) / 100);
            content.addRect(marginLeftTitle, yStartAfterTable - 25, separatorWidth, 0.5f);
            content.fill();

            String totalCosts = String.format("%.2f", totalCharged * tariff);

            newLine(content, FONT_BOLD, P_FONT_SIZE + 1, marginLeftTitle, yStartAfterTable - 40, TEXT_TOTAAL);
            newLine(content, FONT_BOLD, P_FONT_SIZE + 1, marginLeftValue, yStartAfterTable - 40, "\u20AC " + totalCosts);

            document.addPage(page);
            content.close();
            document.save(fileName);
            document.close();

        } catch (IOException e) {
            log.error("Failed to create the report: {}", e.getMessage());
        }
    }

    private void newLine(PDPageContentStream content, int fontSize, float yPos, String text) throws IOException {
        newLine(content, fontSize, DOCUMENT_MARGIN, yPos, text);
    }

    private void newLine(PDPageContentStream content, int fontSize, float xPos, float yPos, String text) throws IOException {
        newLine(content, FONT_PLAIN, fontSize, xPos, yPos, text);
    }

    private void newLine(PDPageContentStream content, PDFont font, int fontSize, float xPos, float yPos, String text) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(xPos, yPos);
        content.showText(text);
        content.endText();

        if (H2_FONT_SIZE == fontSize) {
            content.addRect(DOCUMENT_MARGIN, yPos - 5, DOCUMENT_CONTENT_WIDTH, 0.5f);
            content.fill();
        }
    }

    private void createHeaderRow(BaseTable table) {
        Row<PDPage> headerRow = table.createRow(TABLE_ROW_HEIGHT);

        createHeaderCell(headerRow, TABLE_ROW_LARGE_WIDTH, TEXT_GESTART);
        createHeaderCell(headerRow, TABLE_ROW_LARGE_WIDTH, TEXT_VOLTOOID);
        createHeaderCell(headerRow, TABLE_ROW_SMALL_WIDTH, TEXT_KM_STAND);
        createHeaderCell(headerRow, TABLE_ROW_MEDIUM_WIDTH, TEXT_START_KWH);
        createHeaderCell(headerRow, TABLE_ROW_MEDIUM_WIDTH, TEXT_EIND_KWH);
        createHeaderCell(headerRow, TABLE_ROW_MEDIUM_WIDTH, TEXT_VERBRUIKT);

        table.addHeaderRow(headerRow);
    }

    private void createHeaderCell(Row<PDPage> headerRow, float width, String name) {
        Cell<PDPage> odo_meter = headerRow.createCell(width, name);
        odo_meter.setFontSize(P_FONT_SIZE);
        odo_meter.setFont(FONT_BOLD);
    }

    private void createRow(BaseTable table, ChargeSession chargeSession) {
        Row<PDPage> row = table.createRow(TABLE_ROW_HEIGHT);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Color fillColor;
        float totalCharged = 0;
        if (ChargeSessionType.ANONYMOUS.equals(chargeSession.getChargeSessionType())) {
            fillColor = Color.LIGHT_GRAY;
        } else {
            fillColor = Color.WHITE;
            totalCharged = chargeSession.getTotalkwH();
        }

        // Started at cell
        Cell<PDPage> cell = row.createCell(TABLE_ROW_LARGE_WIDTH, format.format(chargeSession.getStartedAt()));
        cell.setFontSize(P_FONT_SIZE);
        cell.setFillColor(fillColor);

        // Ended at cell
        cell = row.createCell(TABLE_ROW_LARGE_WIDTH, format.format(chargeSession.getEndedAt()));
        cell.setFontSize(P_FONT_SIZE);
        cell.setFillColor(fillColor);

        // Odo meter cell
        cell = row.createCell(TABLE_ROW_SMALL_WIDTH, String.valueOf(chargeSession.getOdoMeter()));
        cell.setFontSize(P_FONT_SIZE);
        cell.setFillColor(fillColor);

        // Start kWh cell
        cell = row.createCell(TABLE_ROW_MEDIUM_WIDTH, String.format("%.3f", chargeSession.getStartkWh()));
        cell.setFontSize(P_FONT_SIZE);
        cell.setFillColor(fillColor);

        // End kWh cell
        cell = row.createCell(TABLE_ROW_MEDIUM_WIDTH, String.format("%.3f", chargeSession.getEndkWh()));
        cell.setFontSize(P_FONT_SIZE);
        cell.setFillColor(fillColor);

        // total kWh cell
        cell = row.createCell(TABLE_ROW_MEDIUM_WIDTH, String.format("%.3f", totalCharged));
        cell.setFontSize(P_FONT_SIZE);
        cell.setFillColor(fillColor);
    }
}
