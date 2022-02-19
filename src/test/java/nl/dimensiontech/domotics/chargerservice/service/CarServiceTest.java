package nl.dimensiontech.domotics.chargerservice.service;

import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.CarState;
import nl.dimensiontech.domotics.chargerservice.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ChargeSessionService chargeSessionService;

    @Mock
    private ConfigProperties configProperties;

    @InjectMocks
    private CarService carService;

    @Test
    public void testGetCarById() {
        // given
        when(carRepository.findById(1L)).thenReturn(Optional.of(new Car()));

        // when
        Optional<Car> optionalCar = carService.getCarById(1L);

        // then
        assertThat(optionalCar).isPresent();
    }

    @Test
    public void testGetCars() {
        // given
        when(carRepository.findAll()).thenReturn(Arrays.asList(new Car(), new Car()));

        // when
        List<Car> cars = carService.getCars();

        // then
        assertThat(cars.size()).isEqualTo(2);
    }

    @Test
    public void testSaveCar() {
        // given
        Car car = new Car();
        when(carRepository.save(car)).thenReturn(car);

        // when
        Car savedCar = carService.saveCar(car);

        // then
        assertThat(savedCar).isNotNull();
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testHandleCarStateChange() {
        // given
        Car car = new Car();
        car.setCarState(CarState.ONLINE);

        // when
        carService.handleStateChange(car);

        // then
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testHandleCarStateChangeToCharging() {
        // given
        Car car = new Car();
        car.setName("foo");
        car.setLatitude(51.000022);
        car.setLongitude(5.000016);
        car.setCarState(CarState.CHARGING);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        when(chargeSessionService.assignToActiveSession(car)).thenReturn(true);

        // when
        carService.handleStateChange(car);

        // then
        verify(carRepository, times(1)).save(car);
        verify(chargeSessionService, times(1)).assignToActiveSession(car);
    }

    @Test
    public void testHandleCarStateChangeToPreconditioning() {
        // given
        Car car = new Car();
        car.setName("foo");
        car.setLatitude(51.000022);
        car.setLongitude(5.000016);
        car.setCarState(CarState.SUSPENDED);
        car.setPluggedIn(true);
        car.setPreconditioning(true);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        when(chargeSessionService.assignToActiveSession(car)).thenReturn(true);

        // when
        carService.handleStateChange(car);

        // then
        verify(carRepository, times(1)).save(car);
        verify(chargeSessionService, times(1)).assignToActiveSession(car);
    }

    @Test
    public void testHandleCarStateChangeToChargingNotAtHome() {
        // given
        Car car = new Car();
        car.setName("foo");
        car.setLatitude(51.006922);
        car.setLongitude(5.004116);
        car.setCarState(CarState.CHARGING);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        // when
        carService.handleStateChange(car);

        // then
        verify(carRepository, times(1)).save(car);
        verifyNoInteractions(chargeSessionService);
    }

    @Test
    public void testHandleCarStateChangeToPreconditioningNotAtHome() {
        // given
        Car car = new Car();
        car.setName("foo");
        car.setLatitude(51.006922);
        car.setLongitude(5.004116);
        car.setCarState(CarState.SUSPENDED);
        car.setPluggedIn(true);
        car.setPreconditioning(true);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        // when
        carService.handleStateChange(car);

        // then
        verify(carRepository, times(1)).save(car);
        verifyNoInteractions(chargeSessionService);
    }

    @Test
    public void testHandleCarStateChangeToChargingRetryAssigning() {
        // given
        Car car = new Car();
        car.setName("foo");
        car.setLatitude(51.000022);
        car.setLongitude(5.000016);
        car.setCarState(CarState.CHARGING);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        ConfigProperties.SessionAssignment sessionAssignment = new ConfigProperties.SessionAssignment();
        sessionAssignment.setRetryTimeout(2);
        sessionAssignment.setNumberOfRetries(3);
        when(configProperties.getSessionAssignment()).thenReturn(sessionAssignment);

        when(chargeSessionService.assignToActiveSession(car)).thenReturn(false).thenReturn(true);

        // when
        carService.handleStateChange(car);

        // then
        verify(carRepository, times(1)).save(car);
        verify(chargeSessionService, times(2)).assignToActiveSession(car);
    }
}