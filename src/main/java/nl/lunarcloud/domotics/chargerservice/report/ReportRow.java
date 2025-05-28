package nl.lunarcloud.domotics.chargerservice.report;

public record ReportRow(String startedAt,
                        String endedAt,
                        String odoMeter,
                        String startKwh,
                        String endKwh,
                        String totalKwh,
                        boolean anonymous) {
}
