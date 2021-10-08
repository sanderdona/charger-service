package nl.dimensiontech.domotics.chargerservice.config;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ChargerServiceConfiguration {

    private static final String MQTT_CLIENT_UUID = UUID.randomUUID().toString();

    private final ConfigProperties configProperties;

    @Bean
    public MessageChannel chargerInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel carStateInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoClientFactory clientFactory() {
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();

        ConfigProperties.MqttConfig mqttConfig = configProperties.getMqttConfig();

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(mqttConfig.getUsername());
        connectOptions.setPassword(mqttConfig.getPassword().toCharArray());

        clientFactory.setConnectionOptions(connectOptions);

        return clientFactory;
    }

    @Bean
    public MessageProducer chargeMessageInbound() {

        ConfigProperties.MqttConfig mqttConfig = configProperties.getMqttConfig();

        final String mqttHost = mqttConfig.getHost();
        final String clientId = mqttConfig.getClient() + "_" + MQTT_CLIENT_UUID;
        final String powerTopic = "home/charger/sdm1-1/Power";
        final String importTopic = "home/charger/sdm1-1/Import";

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttHost,
                clientId,
                clientFactory(),
                powerTopic,
                importTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0);
        adapter.setOutputChannel(chargerInputChannel());
        return adapter;
    }

    @Bean
    public MessageProducer carMessageInbound() {

        ConfigProperties.MqttConfig mqttConfig = configProperties.getMqttConfig();

        final String mqttHost = mqttConfig.getHost();
        final String clientId = mqttConfig.getClient() + "_" + MQTT_CLIENT_UUID;
        final String stateTopic = "teslamate/cars/1/state";
        final String latitudeTopic = "teslamate/cars/1/latitude";
        final String longitudeTopic = "teslamate/cars/1/longitude";
        final String odometerTopic = "teslamate/cars/1/odometer";
        final String displayNameTopic = "teslamate/cars/1/display_name";

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttHost,
                clientId,
                clientFactory(),
                stateTopic,
                latitudeTopic,
                longitudeTopic,
                odometerTopic,
                displayNameTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0);
        adapter.setOutputChannel(carStateInputChannel());
        return adapter;
    }

}
