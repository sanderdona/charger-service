package nl.dimensiontech.domotics.chargerservice.message.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SimpleMessageServiceTest {

    @Mock
    private ConfigProperties configProperties;

    @Mock
    private OutboundMessageHandler outboundMessageHandler;

    @InjectMocks
    private SimpleMessageService simpleMessageService;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @BeforeEach
    public void beforeAll() {
        var mqttConfig = new ConfigProperties.MqttConfig();
        mqttConfig.setRootTopic("root");

        when(configProperties.getMqttConfig()).thenReturn(mqttConfig);
    }

    @Test
    public void shouldSendMessage() {
        // given
        var payload = "foo";
        // when
        simpleMessageService.sendMessage(payload);

        // then
        verify(outboundMessageHandler).handleMessage(messageCaptor.capture());

        assertThat(messageCaptor.getValue()).isNotNull();
        assertThat(messageCaptor.getValue().getHeaders().get(MqttHeaders.TOPIC)).isEqualTo("root");
        assertThat(messageCaptor.getValue().getPayload()).isEqualTo("foo");
    }

    @Test
    public void shouldSendMessageToProvidedTopic() {
        // given
        var payload = "foo";
        // when
        simpleMessageService.sendMessage(payload, "bla");

        // then
        verify(outboundMessageHandler).handleMessage(messageCaptor.capture());

        assertThat(messageCaptor.getValue()).isNotNull();
        assertThat(messageCaptor.getValue().getHeaders().get(MqttHeaders.TOPIC)).isEqualTo("root/bla");
        assertThat(messageCaptor.getValue().getPayload()).isEqualTo("foo");
    }

}