package nl.dimensiontech.domotics.chargerservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.exception.InvalidIdentifierException;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDUtil {

    public static UUID toUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new InvalidIdentifierException();
        }
    }
}
