package nl.dimensiontech.domotics.chargerservice;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import javax.net.SocketFactory;

import static org.mockito.Mockito.mock;

@SpringBootTest
class ChargerServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Configuration
	static class TestConfiguration {

		@Bean
		@Primary
		public MqttPahoClientFactory clientFactory() {

			DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();

			MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
			SocketFactory socketFactory = mock(SocketFactory.class);
			mqttConnectOptions.setSocketFactory(socketFactory);

			clientFactory.setConnectionOptions(mqttConnectOptions);

			return clientFactory;
		}
	}

}
