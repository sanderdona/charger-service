package nl.dimensiontech.domotics.chargerservice.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.constants.MqttConstants;
import nl.dimensiontech.domotics.chargerservice.message.service.SimpleMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ApplicationEventListenerTest {

    @Mock
    private ConfigProperties configProperties;

    @Mock
    private SimpleMessageService messageService;

    @InjectMocks
    private ApplicationEventListener applicationEventListener;

    @BeforeEach
    public void beforeAll() {
        var mqttConfig = new ConfigProperties.MqttConfig();
        mqttConfig.setRootTopic("root");

        when(configProperties.getMqttConfig()).thenReturn(mqttConfig);
    }

    @Test
    public void shouldSendApplicationOnlineMessage() {
        applicationEventListener.handleApplicationStartedEvent();

        verify(messageService).sendMessage(
                MqttConstants.STATUS_ONLINE,
                "root/" + MqttConstants.STATUS_TOPIC,
                true
        );
    }

    @Test
    public void shouldSendApplicationOfflineMessage() {
        applicationEventListener.handlePreDestroy();

        verify(messageService).sendMessage(
                MqttConstants.STATUS_OFFLINE,
                "root/" + MqttConstants.STATUS_TOPIC,
                true
        );
    }

}