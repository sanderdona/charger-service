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
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.UUID;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ChargerServiceConfiguration {

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
        final String clientId = mqttConfig.getClient() + "_" + UUID.randomUUID();
        final String powerTopic = mqttConfig.getPowerTopic();
        final String importTopic = mqttConfig.getImportedEnergyTopic();

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttHost,
                clientId,
                clientFactory(),
                powerTopic,
                importTopic);
        adapter.setCompletionTimeout(mqttConfig.getCompletionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(mqttConfig.getQos());
        adapter.setOutputChannel(chargerInputChannel());
        return adapter;
    }

    @Bean
    public MessageProducer carMessageInbound() {

        ConfigProperties.MqttConfig mqttConfig = configProperties.getMqttConfig();

        final String mqttHost = mqttConfig.getHost();
        final String clientId = mqttConfig.getClient() + "_" + UUID.randomUUID();
        final String stateTopic = mqttConfig.getCarStateTopic();
        final String latitudeTopic = mqttConfig.getCarLatitudeTopic();
        final String longitudeTopic = mqttConfig.getCarLongitudeTopic();
        final String odometerTopic = mqttConfig.getCarOdometerTopic();
        final String displayNameTopic = mqttConfig.getCarDisplayNameTopic();

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttHost,
                clientId,
                clientFactory(),
                stateTopic,
                latitudeTopic,
                longitudeTopic,
                odometerTopic,
                displayNameTopic);
        adapter.setCompletionTimeout(mqttConfig.getCompletionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(mqttConfig.getQos());
        adapter.setOutputChannel(carStateInputChannel());
        return adapter;
    }

}
