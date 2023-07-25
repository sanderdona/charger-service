package nl.dimensiontech.domotics.chargerservice.controller;

import lombok.RequiredArgsConstructor;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.service.CarService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("cars")
@RequiredArgsConstructor
public class CarResource {

    private final CarService carService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('client_read')")
    public Optional<Car> getCar(@PathVariable String id) {
        return carService.getCarById(Long.valueOf(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('client_read')")
    public List<Car> getCars() {
        return carService.getCars();
    }

}
