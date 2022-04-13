package nl.dimensiontech.domotics.chargerservice.service;

public class CannotAssignException extends IllegalStateException {
    public CannotAssignException(String message) {
        super(message);
    }
}
