package nl.dimensiontech.domotics.chargerservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ChargerServiceTestConfiguration {

    @Bean
    public MqttPahoClientFactory clientFactory() {
        return mock(MqttPahoClientFactory.class);
    }

    @Bean
    public MessageProducer chargeMessageInbound() {
        return mock(MessageProducer.class);
    }

    @Bean
    public MessageProducer carMessageInbound() {
        return mock(MessageProducer.class);
    }

}
