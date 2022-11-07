package nl.dimensiontech.domotics.chargerservice.service;

import org.springframework.stereotype.Service;

@Service
public class EnergyMeterService {

    private volatile static double reading;

    public void setCurrentReading(double reading) {
        EnergyMeterService.reading = reading;
    }

    public double getCurrentReading() {
        return reading;
    }

}
