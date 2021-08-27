package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.service.CarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CarResource {

    private final CarService carService;

    @GetMapping(path = "cars")
    public List<Car> getCars() {
        return carService.getCars();
    }

}
