package nl.dimensiontech.domotics.chargerservice.mapper;

import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.dto.CarDto;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ChargeSessionMapper {

    @Mapping(source = "chargeSessionType", target = "type", qualifiedByName = "sessionTypeConversion")
    ChargeSessionDto toDto(ChargeSession source);

    List<ChargeSessionDto> toDto(List<ChargeSession> source);

    CarDto carToDto(Car car);

    @Named("sessionTypeConversion")
    static String convertSessionType(String type) {
        return type.toLowerCase();
    }
}
