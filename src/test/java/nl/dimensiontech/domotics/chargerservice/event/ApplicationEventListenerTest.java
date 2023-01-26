package nl.dimensiontech.domotics.chargerservice.event;

import nl.dimensiontech.domotics.chargerservice.constants.MqttConstants;
import nl.dimensiontech.domotics.chargerservice.message.service.SimpleMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class ApplicationEventListenerTest {

    @Mock
    private SimpleMessageService messageService;

    @InjectMocks
    private ApplicationEventListener applicationEventListener;

    @Test
    public void shouldSendApplicationOnlineMessage() {
        applicationEventListener.handleApplicationStartedEvent();

        verify(messageService).sendMessage(MqttConstants.STATUS_ONLINE, MqttConstants.STATUS_TOPIC);
    }

    @Test
    public void shouldSendApplicationOfflineMessage() {
        applicationEventListener.handlePreDestroy();

        verify(messageService).sendMessage(MqttConstants.STATUS_OFFLINE, MqttConstants.STATUS_TOPIC);
    }

}