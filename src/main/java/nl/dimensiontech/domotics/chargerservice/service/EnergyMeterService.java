package nl.dimensiontech.domotics.chargerservice.service;

import org.springframework.stereotype.Service;

@Service
public class EnergyMeterService {

    private volatile static float reading;

    public void setCurrentReading(float reading) {
        EnergyMeterService.reading = reading;
    }

    public float getCurrentReading() {
        return reading;
    }

}
