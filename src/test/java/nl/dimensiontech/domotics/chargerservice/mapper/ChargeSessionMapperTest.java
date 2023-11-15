package nl.dimensiontech.domotics.chargerservice.mapper;

import nl.dimensiontech.domotics.chargerservice.api.model.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.api.model.SessionTypeDto;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChargeSessionMapperTest {

//    @Test
//    public void shouldMaptoDto() {
//        Car car = new Car();
//
//        ChargeSession chargeSession = new ChargeSession();
//        chargeSession.setId(UUID.randomUUID());
//        chargeSession.setCar(car);
//        chargeSession.setOdoMeter(123456);
//        chargeSession.setChargeSessionType(ChargeSessionType.REGISTERED);
//        chargeSession.setStartedAt(LocalDateTime.of(2022, Month.APRIL, 23, 12, 10));
//        chargeSession.setEndedAt(LocalDateTime.of(2022, Month.APRIL, 23, 19, 35));
//        chargeSession.setStartkWh(1230.754);
//        chargeSession.setEndkWh(1255.605);
//        chargeSession.setTotalkwH(21.645);
//
//        ChargeSessionMapper chargeSessionMapper = new ChargeSessionMapperImpl();
//        ChargeSessionDto chargeSessionDto = chargeSessionMapper.toDto(chargeSession);
//
//        assertThat(chargeSessionDto.getId()).isEqualTo(1L);
//        assertThat(chargeSessionDto.getVehicleId()).isNotNull();
//        assertThat(chargeSessionDto.getOdometer()).isEqualTo(123456);
//        assertThat(chargeSessionDto.getSessionType()).isEqualTo(SessionTypeDto.REGISTERED);
//        assertThat(chargeSessionDto.getStartedAt()).isEqualTo(LocalDateTime.of(2022, Month.APRIL, 23, 12, 10));
//        assertThat(chargeSessionDto.getEndedAt()).isEqualTo(LocalDateTime.of(2022, Month.APRIL, 23, 19, 35));
//        assertThat(chargeSessionDto.getStartKwh()).isEqualTo(BigDecimal.valueOf(1230.754));
//        assertThat(chargeSessionDto.getEndKwh()).isEqualTo(BigDecimal.valueOf(1255.605));
//        assertThat(chargeSessionDto.getTotalKwh()).isEqualTo(BigDecimal.valueOf(21.645));
//    }
//
//    @Test
//    public void shouldConvertToLowerCase() {
//        assertThat(ChargeSessionMapper.convertSessionType("UPPERCASE")).isLowerCase();
//    }

}