package nl.dimensiontech.domotics.chargerservice.controller;

import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.service.CarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarResourceTest {

    @Mock
    private CarService carService;

    @InjectMocks
    private CarResource carResource;

    @Test
    public void testGetCars() {
        // given
        when(carService.getCars()).thenReturn(List.of(new Car()));

        // when
        List<Car> cars = carResource.getCars();

        // then
        assertThat(cars.size()).isEqualTo(1);
    }

}