package nl.dimensiontech.domotics.chargerservice.util;

import java.time.format.DateTimeFormatter;

public class Formatter {

    private Formatter() {
        // No instantiation possible
    }

    public static DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    }

}
