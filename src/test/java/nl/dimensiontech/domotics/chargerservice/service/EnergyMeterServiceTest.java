package nl.dimensiontech.domotics.chargerservice.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnergyMeterServiceTest {

    private final EnergyMeterService energyMeterService = new EnergyMeterService();

    @Test
    public void testSetCurrentReading() {
        energyMeterService.setCurrentReading(120.455d);

        assertThat(energyMeterService.getCurrentReading()).isEqualTo(120.455d);
    }

}