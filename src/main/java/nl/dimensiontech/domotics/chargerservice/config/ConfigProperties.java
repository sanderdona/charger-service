package nl.dimensiontech.domotics.chargerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "charger-service")
public class ConfigProperties {

    private float tariff;
    private String licensePlate;
    private MqttConfig mqttConfig;
    private LocationConfig locationConfig;
    private EmailConfig emailConfig;

    @Data
    public static class MqttConfig {
        private String client;
        private String host;
        private String username;
        private String password;
    }

    @Data
    public static class LocationConfig {
        private double homeLatitude;
        private double homeLongitude;
        private int maxDistanceFromHome;
    }

    @Data
    public static class EmailConfig {
        private String host;
        private int port;
        private String username;
        private String password;
        private String protocol = "tls";
        private boolean authEnabled = true;
        private boolean tlsEnabled = true;
        private boolean debugEnabled = false;
        private String fromAddress;
        private String toAddress;
    }

}
