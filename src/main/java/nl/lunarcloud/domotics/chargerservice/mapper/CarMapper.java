package nl.lunarcloud.domotics.chargerservice.mapper;

import nl.lunarcloud.domotics.chargerservice.api.model.VehicleApi;
import nl.lunarcloud.domotics.chargerservice.domain.Car;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CarMapper {

    @Mapping(source = "uuid", target = "id")
    VehicleApi toDto(Car car);

}
