package nl.lunarcloud.domotics.chargerservice.cucumber;

import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@ActiveProfiles("cucumber")
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = {CucumberSpringConfiguration.Initializer.class})
public class CucumberSpringConfiguration {

    public static HiveMQContainer mqttContainer;

    @BeforeAll
    public static void setUp() {
        mqttContainer = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:2023.10"))
                .withNetwork(Network.SHARED)
                .withExposedPorts(1883)
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(CucumberSpringConfiguration.class)));
        mqttContainer.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "charger-service.mqtt-config.host=tcp://" + mqttContainer.getHost() + ":" + mqttContainer.getMqttPort()
            ).applyTo(applicationContext.getEnvironment());
        }
    }

    @PreDestroy
    public static void tearDown() {
        mqttContainer.stop();
    }
}
