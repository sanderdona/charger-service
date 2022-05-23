package nl.dimensiontech.domotics.chargerservice.message.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ChargeSessionMessageServiceTest {

    @Mock
    private OutboundMessageHandler outboundMessageHandler;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private ChargeSessionMessageService chargeSessionMessageService;

    @BeforeEach
    public void beforeAll() {
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    public void shouldSendChargeSessionMessage() {
        // given
        ChargeSessionDto chargeSessionDto = new ChargeSessionDto();
        chargeSessionDto.setId(1L);
        chargeSessionDto.setOdoMeter(0);
        chargeSessionDto.setStartkWh(0.0f);
        chargeSessionDto.setEndkWh(0.0f);
        chargeSessionDto.setTotalkwH(0.0f);
        chargeSessionDto.setType("anonymous");

        // when
        chargeSessionMessageService.sendMessage(chargeSessionDto);

        // then
        verify(outboundMessageHandler, times(1)).sendMessage(
                "{" +
                        "\"id\":1," +
                        "\"odoMeter\":0," +
                        "\"type\":\"anonymous\"," +
                        "\"startkWh\":0.0," +
                        "\"endkWh\":0.0," +
                        "\"totalkwH\":0.0" +
                        "}"
        );
    }

}