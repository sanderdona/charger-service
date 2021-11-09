package nl.dimensiontech.domotics.chargerservice.constants;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PdfConstants {

    public static final String FILE_EXTENSION = ".pdf";
    public static final String LANGUAGE_TAG = "nl";

    public static final float DOCUMENT_MARGIN = 40;
    public static final float TEXT_MARGIN = 10;
    public static final float DOCUMENT_CONTENT_WIDTH = PDRectangle.A4.getWidth() - (2 * DOCUMENT_MARGIN);

    public static final int H1_FONT_SIZE = 12;
    public static final int H2_FONT_SIZE = 10;
    public static final int P_FONT_SIZE = 8;

    public static final int TABLE_ROW_HEIGHT = 8;
    public static final float TABLE_ROW_SMALL_WIDTH = (100 / 6.0f) - 3.0f;
    public static final float TABLE_ROW_MEDIUM_WIDTH = (100 / 6.0f) - 1.0f;
    public static final float TABLE_ROW_LARGE_WIDTH = (100 / 6.0f) + 3.0f;

    public static final float MARGIN_LEFT_3_TABLES_SKIPPED = 393.8226f;
    public static final float MARGIN_LEFT_4_TABLES_SKIPPED = 474.5491f;

    public static final String TEXT_TITLE = "Declaratie laadkosten";

    public static final String TEXT_ALGEMEEN = "Algemeen";
    public static final String TEXT_KENTEKEN = "Kenteken";
    public static final String TEXT_PERIODE = "Periode";
    public static final String TEXT_LAADSESSIES = "Laadsessies";

    public static final String TEXT_DECLARATIE = "declaratie";
    public static final String TEXT_GESTART = "gestart";
    public static final String TEXT_VOLTOOID = "voltooid";
    public static final String TEXT_KM_STAND = "km-stand";
    public static final String TEXT_START_KWH = "start kWh";
    public static final String TEXT_EIND_KWH = "eind kWh";
    public static final String TEXT_VERBRUIKT = "verbruikt kWh";

    public static final String TEXT_TOTAAL_KWH = "Totaal kWh";
    public static final String TEXT_TARIEF_KWH = "Tarief kWh";
    public static final String TEXT_TOTAAL = "Totaal";

    public static final String TEXT_METERSTANDEN = "Meterstanden";
    public static final String TEXT_STAND = "Stand";
}
