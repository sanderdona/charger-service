package nl.dimensiontech.domotics.chargerservice.reporting;

import java.util.List;

public record Report(
        String kenteken,
        String periode,
        List<ReportRow> row,
        String kwhTotaal,
        String kwhTarief,
        String kostenTotaal) {
}
