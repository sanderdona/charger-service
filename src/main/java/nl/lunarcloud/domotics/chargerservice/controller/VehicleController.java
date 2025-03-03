package nl.lunarcloud.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.lunarcloud.domotics.chargerservice.api.VehiclesApi;
import nl.lunarcloud.domotics.chargerservice.api.model.VehicleApi;
import nl.lunarcloud.domotics.chargerservice.common.exception.RecordNotFoundException;
import nl.lunarcloud.domotics.chargerservice.domain.Car;
import nl.lunarcloud.domotics.chargerservice.mapper.CarMapper;
import nl.lunarcloud.domotics.chargerservice.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VehicleController implements VehiclesApi {

    private final CarService carService;
    private final CarMapper carMapper;

    @Override
    public ResponseEntity<VehicleApi> getVehicle(String vehicleId) {
        Car car = carService.getCarByUuid(UUID.fromString(vehicleId)).orElseThrow(
                () -> new RecordNotFoundException("Vehicle not found")
        );
        return ResponseEntity.ok(carMapper.toDto(car));
    }


}
