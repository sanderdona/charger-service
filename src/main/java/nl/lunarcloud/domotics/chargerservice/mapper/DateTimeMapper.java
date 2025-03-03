package nl.lunarcloud.domotics.chargerservice.mapper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DateTimeMapper {
    public OffsetDateTime map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
    }
}
