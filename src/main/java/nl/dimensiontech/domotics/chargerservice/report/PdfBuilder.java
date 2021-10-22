package nl.dimensiontech.domotics.chargerservice.report;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.report.domain.TableCell;
import nl.dimensiontech.domotics.chargerservice.report.domain.TableColumn;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static nl.dimensiontech.domotics.chargerservice.constants.PdfConstants.*;

@Slf4j
@Getter
public class PdfBuilder {

    private PDPageContentStream content;
    private PDPage page;
    private final PDDocument document = new PDDocument();
    private float lastYPosition;
    private float contentMargin;
    private float textMargin;

    public PdfBuilder createPage(PDRectangle pageSize, float contentMargin, float textMargin) throws IOException {
        this.contentMargin = contentMargin;
        this.textMargin = textMargin;
        return createPage(pageSize);
    }

    public PdfBuilder createPage(PDRectangle pageSize) throws IOException {
        if (ObjectUtils.isEmpty(page)) {
            page = new PDPage(pageSize);
        }

        content = new PDPageContentStream(document, page);
        return this;
    }

    public PdfBuilder addTextLine(String text, PDFont font, int fontSize) throws IOException {
        return addTextLine(text, font, fontSize, contentMargin, false);
    }

    public PdfBuilder addTextLine(String text, PDFont font, int fontSize, float xPosition, boolean sameLine) throws IOException {
        if (!sameLine) {
            lastYPosition = lastYPosition + getLineHeigt(fontSize);
        } else {
            lastYPosition = lastYPosition - fontSize;
        }
        return addTextLine(text, font, fontSize, xPosition, lastYPosition + contentMargin);
    }

    public PdfBuilder addTextLine(String text, PDFont font, int fontSize, float xPosition, float yPosition) throws IOException {
        validatePageIsOpen();

        float pageSizeHeight = page.getMediaBox().getHeight();

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(xPosition, pageSizeHeight - yPosition);
        content.showText(text);
        content.endText();

        lastYPosition = lastYPosition + fontSize;

        return this;
    }

    public PdfBuilder addLine() throws IOException {
        validatePageIsOpen();

        float pageSizeHeight = page.getMediaBox().getHeight() - contentMargin;
        float yPosition = pageSizeHeight - lastYPosition;
        float width = page.getMediaBox().getWidth() - (2 * contentMargin);

        content.addRect(contentMargin, yPosition, width, 0.5f);
        content.fill();

        lastYPosition = lastYPosition + textMargin;
        return this;
    }

    public TableBuilder createTable() throws IOException {
        return new TableBuilder(this);
    }

    public PdfBuilder closePage() throws IOException {
        addAndClosePage();
        return this;
    }

    public void save(String fileName) {
        try {
            if (!ObjectUtils.isEmpty(page)) {
                addAndClosePage();
            }

            document.save(fileName);
            document.close();
        } catch (IOException e) {
            log.error("Failed to save the report: {}", e.getMessage());
        }
    }

    private int getLineHeigt(int fontSize) {
        if (fontSize >= H2_FONT_SIZE && fontSize < H1_FONT_SIZE) {
            return fontSize + 20;
        } else {
            return fontSize;
        }
    }

    private void validatePageIsOpen() {
        if (ObjectUtils.isEmpty(page)) {
            throw new IllegalStateException("No page opened to add text to...");
        }
    }

    private void addAndClosePage() throws IOException {
        document.addPage(page);
        content.close();
        page = null;
        content = null;
    }


    public class TableBuilder {
        private PdfBuilder builder;
        private BaseTable table;

        public TableBuilder(PdfBuilder builder) throws IOException {
            this.builder = builder;
            float pageSizeHeight = page.getMediaBox().getHeight() - contentMargin;
            float yPosition = pageSizeHeight - lastYPosition;

            table = new BaseTable(yPosition, 0, 0,
                    DOCUMENT_CONTENT_WIDTH, contentMargin, document, page, true, true);
        }

        public TableBuilder fromTable(Table<Integer, TableColumn, TableCell> inputTable) {
            Set<TableColumn> columnKeySet = inputTable.columnKeySet();
            Set<Integer> rowKeySet = inputTable.rowKeySet();

            // create header row
            Row<PDPage> headerRow = table.createRow(TABLE_ROW_HEIGHT);
            for (TableColumn columnKey : columnKeySet) {
                headerRow.createCell(columnKey.getWidth(), columnKey.getName());
            }

            // create table rows
            for (int row : rowKeySet) {
                Map<TableColumn, TableCell> rowMap = inputTable.row(row);
                Row<PDPage> contentRow = table.createRow(TABLE_ROW_HEIGHT);
                for (Map.Entry<TableColumn, TableCell> entry : rowMap.entrySet()) {
                    Cell<PDPage> cell = contentRow.createCell(entry.getKey().getWidth(), entry.getValue().getValue());
                    cell.setFillColor(entry.getValue().getFillColor());
                }
            }
            return this;
        }

        public PdfBuilder closeTable() throws IOException {
            table.draw();
            lastYPosition = lastYPosition + table.getHeaderAndDataHeight() + textMargin;
            return builder;
        }

    }
}
