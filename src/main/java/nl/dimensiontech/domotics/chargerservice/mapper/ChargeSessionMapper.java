package nl.dimensiontech.domotics.chargerservice.mapper;

import nl.dimensiontech.domotics.chargerservice.api.model.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.message.model.ChargeSessionMessage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ChargeSessionMapper {

    @Mapping(source = "chargeSessionType", target = "type")
    @Mapping(source = "car", target = "vehicleId", qualifiedByName = "carToUUID")
    ChargeSessionDto toDto(ChargeSession source);

    List<ChargeSessionDto> toDto(List<ChargeSession> source);

    @Mapping(source = "chargeSessionType", target = "type", qualifiedByName = "sessionTypeConversion")
    ChargeSessionMessage toMessage(ChargeSession source);

    @Named("carToUUID")
    static UUID convertSessionType(Car car) {
        return car.getUuid();
    }

    @Named("sessionTypeConversion")
    static String convertSessionType(String type) {
        return type.toLowerCase();
    }
}
