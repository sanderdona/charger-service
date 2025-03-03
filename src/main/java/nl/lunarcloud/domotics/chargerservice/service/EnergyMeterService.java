package nl.lunarcloud.domotics.chargerservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EnergyMeterService {

    private volatile static Double reading;
    private volatile static Long timestamp;

    public void setCurrentReading(double reading, long timestamp) {
        if (EnergyMeterService.reading != null && reading < EnergyMeterService.reading) {
            log.error("Receiving faulty energy meter readings: new reading of {} kW is lower than previous reading of {} kW", reading, EnergyMeterService.reading);
        }

        if (EnergyMeterService.timestamp != null && timestamp < EnergyMeterService.timestamp) {
            log.error("Receiving faulty energy meter readings: new timestamp of {} is lower than previous timestamp of {}", timestamp, EnergyMeterService.timestamp);
        }

        EnergyMeterService.reading = reading;
        EnergyMeterService.timestamp = timestamp;
    }

    public Double getCurrentReading() {
        return reading;
    }

    public Long getCurrentTimestamp() {
        return timestamp;
    }
}
