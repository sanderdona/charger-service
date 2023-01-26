package nl.dimensiontech.domotics.chargerservice.message.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.dto.ChargeSessionDto;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargeSessionMessageServiceTest {

    @Mock
    private OutboundMessageHandler outboundMessageHandler;

    @Mock
    private ConfigProperties configProperties;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private ChargeSessionMessageService chargeSessionMessageService;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @BeforeEach
    public void beforeAll() {
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        var mqttConfig = new ConfigProperties.MqttConfig();
        mqttConfig.setRootTopic("root");

        when(configProperties.getMqttConfig()).thenReturn(mqttConfig);
    }

    @Test
    public void shouldSendChargeSessionMessage() {
        // given
        var chargeSessionDto = new ChargeSessionDto();
        chargeSessionDto.setId(1L);
        chargeSessionDto.setOdoMeter(0);
        chargeSessionDto.setStartkWh(0.000);
        chargeSessionDto.setEndkWh(0.000);
        chargeSessionDto.setTotalkwH(0.000);
        chargeSessionDto.setType("anonymous");

        // when
        chargeSessionMessageService.sendMessage(chargeSessionDto);

        // then
        verify(outboundMessageHandler).handleMessage(messageCaptor.capture());

        assertThat(messageCaptor.getValue()).isNotNull();
        assertThat(messageCaptor.getValue().getHeaders().get(MqttHeaders.TOPIC)).isEqualTo("root");
        assertThat(messageCaptor.getValue().getPayload()).isEqualTo(
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

    @Test
    public void shouldSendChargeSessionMessageToProvidedTopic() {
        // given
        var chargeSessionDto = new ChargeSessionDto();

        // when
        chargeSessionMessageService.sendMessage(chargeSessionDto, "bla");

        // then
        verify(outboundMessageHandler).handleMessage(messageCaptor.capture());

        assertThat(messageCaptor.getValue()).isNotNull();
        assertThat(messageCaptor.getValue().getHeaders().get(MqttHeaders.TOPIC)).isEqualTo("root/bla");
    }

}