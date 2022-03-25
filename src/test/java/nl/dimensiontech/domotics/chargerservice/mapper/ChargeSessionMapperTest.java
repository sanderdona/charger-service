package nl.dimensiontech.domotics.chargerservice.mapper;

import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class ChargeSessionMapperTest {

    @Test
    public void shouldMaptoDto() {
        Car car = new Car();

        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setId(1L);
        chargeSession.setCar(car);
        chargeSession.setOdoMeter(123456);
        chargeSession.setChargeSessionType(ChargeSessionType.REGISTERED);
        chargeSession.setStartedAt(LocalDateTime.of(2022, Month.APRIL, 23, 12, 10));
        chargeSession.setEndedAt(LocalDateTime.of(2022, Month.APRIL, 23, 19, 35));
        chargeSession.setStartkWh(1234.0f);
        chargeSession.setEndkWh(1255.6f);
        chargeSession.setTotalkwH(21.6f);

        ChargeSessionMapper chargeSessionMapper = new ChargeSessionMapperImpl();
        ChargeSessionDto chargeSessionDto = chargeSessionMapper.toDto(chargeSession);

        assertThat(chargeSessionDto.getId()).isEqualTo(1L);
        assertThat(chargeSessionDto.getCar()).isNotNull();
        assertThat(chargeSessionDto.getOdoMeter()).isEqualTo(123456);
        assertThat(chargeSessionDto.getType()).isEqualTo("registered");
        assertThat(chargeSessionDto.getStartedAt()).isEqualTo(LocalDateTime.of(2022, Month.APRIL, 23, 12, 10));
        assertThat(chargeSessionDto.getEndedAt()).isEqualTo(LocalDateTime.of(2022, Month.APRIL, 23, 19, 35));
        assertThat(chargeSessionDto.getStartkWh()).isEqualTo(1234.0f);
        assertThat(chargeSessionDto.getEndkWh()).isEqualTo(1255.6f);
        assertThat(chargeSessionDto.getTotalkwH()).isEqualTo(21.6f);
    }

    @Test
    public void shouldConvertToLowerCase() {
        assertThat(ChargeSessionMapper.convertSessionType("UPPERCASE")).isLowerCase();
    }

}