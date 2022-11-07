package nl.dimensiontech.domotics.chargerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Data
@Configuration
@ConfigurationProperties(prefix = "charger-service")
public class ConfigProperties {

    private BigDecimal tariff;
    private String licensePlate;
    private SessionAssignment sessionAssignment;
    private MqttConfig mqttConfig;
    private LocationConfig locationConfig;
    private EmailConfig emailConfig;

    @Data
    public static class SessionAssignment {
        private int retryTimeout = 30;
        private int numberOfRetries = 3;
    }

    @Data
    public static class MqttConfig {
        private String client;
        private String host;
        private String username;
        private String password;
        private int qos = 0;
        private long completionTimeout = 5000;
        private String messageTopic = "home/charger-service";
        private String powerTopic = "home/charger/sdm1-1/Power";
        private String importedEnergyTopic = "home/charger/sdm1-1/Import";
        private String carStateTopic = "teslamate/cars/1/state";
        private String carChargerPower = "teslamate/cars/1/charger_power";
        private String carLatitudeTopic = "teslamate/cars/1/latitude";
        private String carLongitudeTopic = "teslamate/cars/1/longitude";
        private String carOdometerTopic = "teslamate/cars/1/odometer";
        private String carDisplayNameTopic = "teslamate/cars/1/display_name";
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
