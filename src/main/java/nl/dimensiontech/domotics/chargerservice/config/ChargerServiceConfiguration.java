package nl.dimensiontech.domotics.chargerservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Configuration
@EnableAsync
@Slf4j
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
    public MessageChannel chargerMessageOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoClientFactory clientFactory() {
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();

        ConfigProperties.MqttConfig mqttConfig = configProperties.getMqttConfig();
        String username = mqttConfig.getUsername();
        String password = mqttConfig.getPassword();

        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName(username);
            connectOptions.setPassword(password.toCharArray());
            clientFactory.setConnectionOptions(connectOptions);
        } else {
            log.warn("Connecting to MQTT broker without username and password");
        }

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
        final String chargerPowerTopic = mqttConfig.getCarChargerPower();

        final String latitudeTopic = mqttConfig.getCarLatitudeTopic();
        final String longitudeTopic = mqttConfig.getCarLongitudeTopic();
        final String odometerTopic = mqttConfig.getCarOdometerTopic();
        final String displayNameTopic = mqttConfig.getCarDisplayNameTopic();

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttHost,
                clientId,
                clientFactory(),
                stateTopic,
                chargerPowerTopic,
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

    @Bean
    @ServiceActivator(inputChannel = "chargerMessageOutboundChannel")
    public MessageHandler chargerServiceOutbound() {

        ConfigProperties.MqttConfig mqttConfig = configProperties.getMqttConfig();

        final String mqttHost = mqttConfig.getHost();
        final String clientId = mqttConfig.getClient() + "_" + UUID.randomUUID();
        final String messageTopic = mqttConfig.getMessageTopic();

        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttHost, clientId, clientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(messageTopic);
        messageHandler.setDefaultRetained(true);
        return messageHandler;
    }

}
