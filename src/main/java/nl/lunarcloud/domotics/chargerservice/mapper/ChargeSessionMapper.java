package nl.lunarcloud.domotics.chargerservice.mapper;

import nl.lunarcloud.domotics.chargerservice.api.model.ChargeSessionApi;
import nl.lunarcloud.domotics.chargerservice.api.model.ChargeSessionPageApi;
import nl.lunarcloud.domotics.chargerservice.api.model.SessionTypeApi;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSessionType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = DateTimeMapper.class)
public interface ChargeSessionMapper {

    @Mapping(source = "uuid", target = "id")
    @Mapping(source = "odoMeter", target = "odometer")
    @Mapping(source = "car.uuid", target = "vehicleId")
    @Mapping(source = "chargeSessionType", target = "type", qualifiedByName = "sessionTypeConversion")
    ChargeSessionApi map(ChargeSession source);

    List<ChargeSessionApi> map(List<ChargeSession> source);

    @Named("sessionTypeConversion")
    default SessionTypeApi convertSessionType(ChargeSessionType type) {
        return SessionTypeApi.valueOf(type.name());
    }

    @Mapping(source = "number", target = "currentPage")
    ChargeSessionPageApi map(Page<ChargeSession> source);
}
