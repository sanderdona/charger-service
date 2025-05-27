package nl.lunarcloud.domotics.chargerservice.common.exception;

public class ReportGenerationFailedException extends RuntimeException {
    public ReportGenerationFailedException(String message) {
        super(message);
    }
}
