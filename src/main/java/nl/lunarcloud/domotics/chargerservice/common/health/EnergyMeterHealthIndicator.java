package nl.lunarcloud.domotics.chargerservice.common.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.common.config.ConfigProperties;
import nl.lunarcloud.domotics.chargerservice.service.EnergyMeterService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnergyMeterHealthIndicator implements HealthIndicator {

    private final EnergyMeterService energyMeterService;
    private final ConfigProperties configProperties;

    @Override
    public Health health() {
        final var status = Health.up();
        final var healthConfig = configProperties.getHealthConfig();
        final int unhealthyIfLastReadingOlderThanSeconds = healthConfig.getUnhealthyIfLastReadingOlderThanSeconds();

        final Double currentReading = energyMeterService.getCurrentReading();
        final Long currentTimestamp = energyMeterService.getCurrentTimestamp();

        final var errors = new ArrayList<>();
        final var details = new HashMap<String, Object>();

        if (currentReading == null)  {
            errors.add("Current reading is unknown");
            details.put("reading", null);
            status.status(Status.DOWN);
        } else {
            details.put("reading", currentReading);
        }

        if (currentTimestamp == null)  {
            errors.add("Current timestamp is unknown");
            details.put("timestamp", null);
            status.status(Status.DOWN);
        } else {
            details.put("timestamp", ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTimestamp), ZoneId.systemDefault()));

            final Instant instant = Instant.ofEpochMilli(currentTimestamp);
            final ZonedDateTime lastReceivedReadingTimeStamp = instant.atZone(ZoneId.systemDefault());
            final ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.systemDefault());
            long secondsSinceLastReading = Duration.between(lastReceivedReadingTimeStamp, currentDateTime).toSeconds();

            if (secondsSinceLastReading > unhealthyIfLastReadingOlderThanSeconds) {
                errors.add("Last reading is older than " + unhealthyIfLastReadingOlderThanSeconds + " seconds");
                status.status(Status.DOWN);
            }
        }

        if (!errors.isEmpty()) {
            details.put("errors", errors);
        }

        status.withDetails(details);
        return status.build();
    }
}
