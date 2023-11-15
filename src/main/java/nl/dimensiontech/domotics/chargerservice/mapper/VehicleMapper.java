package nl.dimensiontech.domotics.chargerservice.mapper;

import nl.dimensiontech.domotics.chargerservice.api.model.VehicleDto;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface VehicleMapper {

    @Mapping(source = "uuid", target = "id")
    VehicleDto toDto(Car car);

}
