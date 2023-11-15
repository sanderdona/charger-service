package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.api.VehiclesApi;
import nl.dimensiontech.domotics.chargerservice.api.model.VehicleDto;
import nl.dimensiontech.domotics.chargerservice.mapper.VehicleMapper;
import nl.dimensiontech.domotics.chargerservice.service.CarService;
import nl.dimensiontech.domotics.chargerservice.util.UUIDUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VehiclesController implements VehiclesApi {

    private final CarService carService;
    private final VehicleMapper vehicleMapper;

    @Override
    @Secured("client_read")
    public ResponseEntity<VehicleDto> getVehicle(String vehicleId) {
        var car = carService.getCarByUuid(UUIDUtil.toUUID(vehicleId));
        return ResponseEntity.ok(vehicleMapper.toDto(car));
    }
}
