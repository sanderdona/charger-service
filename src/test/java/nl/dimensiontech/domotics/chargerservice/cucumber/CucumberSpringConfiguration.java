package nl.dimensiontech.domotics.chargerservice.cucumber;

import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
@ContextConfiguration(initializers = {CucumberSpringConfiguration.Initializer.class})
public class CucumberSpringConfiguration {

    public static HiveMQContainer mqttContainer;

    @MockBean
    JavaMailSender mailSender;

    @BeforeAll
    public static void setUp() {
        mqttContainer = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:latest"))
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
