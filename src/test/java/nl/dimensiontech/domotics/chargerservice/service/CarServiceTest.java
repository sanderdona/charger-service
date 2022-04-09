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
        Car currentState = createCar("foo", CarState.SUSPENDED, 51.000022, 5.000016, 0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(currentState));

        Car updatedState = createCar("foo", CarState.CHARGING, 51.000022, 5.000016, 3);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        when(chargeSessionService.assignToActiveSession(updatedState)).thenReturn(true);

        // when
        carService.handleStateChange(updatedState);

        // then
        verify(carRepository, times(1)).save(updatedState);
        verify(chargeSessionService, times(1)).assignToActiveSession(updatedState);
    }

    @Test
    public void testHandleCarStateChangeToPreconditioning() {
        // given
        Car currentState = createCar("foo", CarState.SUSPENDED, 51.000022, 5.000016, 0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(currentState));

        Car updatedState = createCar("foo", CarState.SUSPENDED, 51.000022, 5.000016, 3);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        when(chargeSessionService.assignToActiveSession(updatedState)).thenReturn(true);

        // when
        carService.handleStateChange(updatedState);

        // then
        verify(carRepository, times(1)).save(updatedState);
        verify(chargeSessionService, times(1)).assignToActiveSession(updatedState);
    }

    @Test
    public void testHandleCarStateChangeToChargingNotAtHome() {
        // given
        Car currentState = createCar("foo", CarState.SUSPENDED, 51.006922, 5.004116, 0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(currentState));

        Car updatedState = createCar("foo", CarState.CHARGING, 51.006922, 5.004116, 3);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        // when
        carService.handleStateChange(updatedState);

        // then
        verify(carRepository, times(1)).save(updatedState);
        verifyNoInteractions(chargeSessionService);
    }

    @Test
    public void testHandleCarStateChangeToPreconditioningNotAtHome() {
        // given
        Car currentState = createCar("foo", CarState.SUSPENDED, 51.006922, 5.004116, 0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(currentState));

        Car updatedState = createCar("foo", CarState.SUSPENDED, 51.006922, 5.004116, 3);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        // when
        carService.handleStateChange(updatedState);

        // then
        verify(carRepository, times(1)).save(updatedState);
        verifyNoInteractions(chargeSessionService);
    }

    @Test
    public void testHandleCarStateChangeToChargingRetryAssigning() {
        // given
        Car currentState = createCar("foo", CarState.SUSPENDED, 51.000022, 5.000016, 0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(currentState));

        Car updatedState = createCar("foo", CarState.CHARGING, 51.000022, 5.000016, 3);

        ConfigProperties.LocationConfig locationConfig = new ConfigProperties.LocationConfig();
        locationConfig.setMaxDistanceFromHome(30);
        locationConfig.setHomeLatitude(51.000000);
        locationConfig.setHomeLongitude(5.000000);
        when(configProperties.getLocationConfig()).thenReturn(locationConfig);

        ConfigProperties.SessionAssignment sessionAssignment = new ConfigProperties.SessionAssignment();
        sessionAssignment.setRetryTimeout(2);
        sessionAssignment.setNumberOfRetries(3);
        when(configProperties.getSessionAssignment()).thenReturn(sessionAssignment);

        when(chargeSessionService.assignToActiveSession(updatedState)).thenReturn(false).thenReturn(true);

        // when
        carService.handleStateChange(updatedState);

        // then
        verify(carRepository, times(1)).save(updatedState);
        verify(chargeSessionService, times(2)).assignToActiveSession(updatedState);
    }

    private Car createCar(String name,
                          CarState carState,
                          double latitude,
                          double longitude,
                          int chargerPower) {
        return createCar(1L, name, carState, latitude, longitude, 0, chargerPower);
    }

    private Car createCar(Long id,
                          String name,
                          CarState carState,
                          double latitude,
                          double longitude,
                          int odoMeter,
                          int chargerPower) {
        Car car = new Car();
        car.setId(id);
        car.setName(name);
        car.setChargerPower(chargerPower);
        car.setCarState(carState);
        car.setLatitude(latitude);
        car.setLongitude(longitude);
        car.setOdometer(odoMeter);
        return car;
    }
}