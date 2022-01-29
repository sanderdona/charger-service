package nl.dimensiontech.domotics.chargerservice.mapper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChargeSessionMapperTest {

    @Test
    public void shouldConvertToLowerCase() {
        assertThat(ChargeSessionMapper.convertSessionType("UPPERCASE")).isLowerCase();
    }

}