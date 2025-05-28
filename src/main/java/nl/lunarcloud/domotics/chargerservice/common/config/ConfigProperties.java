package nl.lunarcloud.domotics.chargerservice.common.config;

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
    private SessionAssignment sessionAssignment = new SessionAssignment();
    private MqttConfig mqttConfig;
    private LocationConfig locationConfig = new LocationConfig();
    private EmailConfig emailConfig;
    private ProofConfig proofConfig = new ProofConfig();
    private HealthConfig healthConfig = new HealthConfig();

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
        private long completionTimeout = 2000;
        private String rootTopic = "apps/charger-service";
        private String energyTopic = "home/laadpaal";
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

    @Data
    public static class ProofConfig {
        private boolean proofsRequired = false;
    }

    @Data
    public static class HealthConfig {
        private int unhealthyIfLastReadingOlderThanSeconds = 30;
    }

}
