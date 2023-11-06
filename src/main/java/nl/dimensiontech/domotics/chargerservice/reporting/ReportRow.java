package nl.dimensiontech.domotics.chargerservice.reporting;

public record ReportRow(String startedAt,
                        String endedAt,
                        String odoMeter,
                        String startKwh,
                        String endKwh,
                        String totalKwh,
                        boolean anonymous) {
}
