package nl.dimensiontech.domotics.chargerservice.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String entityName) {
        super(entityName + " not found");
    }
}
