package nl.lunarcloud.domotics.chargerservice.cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import net.javacrumbs.jsonunit.core.Option;
import nl.lunarcloud.domotics.chargerservice.common.config.ConfigProperties;
import nl.lunarcloud.domotics.chargerservice.cucumber.CallbackHandler;
import nl.lunarcloud.domotics.chargerservice.repository.ChargeSessionRepository;
import org.awaitility.core.ConditionTimeoutException;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.*;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CommonStepDefs extends CallbackHandler {

    @LocalServerPort
    private int port;

    protected final ChargeSessionRepository chargeSessionRepository;
    protected final Clock clock;

    private Response response;

    private String username = "not-an-user";

    @Autowired
    public CommonStepDefs(ConfigProperties properties, ChargeSessionRepository chargeSessionRepository, Clock clock) {
        super(properties);
        this.chargeSessionRepository = chargeSessionRepository;
        this.clock = clock;
    }

    @After
    public void cleanup() {
        receivedMessages.clear();
        chargeSessionRepository.deleteAll();
    }

    @Given("the following date an time: {}")
    public void theFollowingDateAndTime(String dateAndTime) {
        ZonedDateTime zonedDateTime = LocalDateTime.parse(dateAndTime, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).atZone(ZoneId.systemDefault());
        Clock fixedClock = Clock.fixed(zonedDateTime.toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Given("a user with username {string}")
    public void aUserWithUsername(String username) {
        this.username = username;
    }

    @Given("a car with the name {string} with the odometer reading {int} km")
    public void aCarWithNameAndOdoMeter(String name, Integer odometer) {
        publishMessage("teslamate/cars/1/display_name", name);
        publishMessage("teslamate/cars/1/odometer", odometer.toString());
    }

    @When("I send a message on topic {string} with payload {string}")
    public void serviceReceivesMqttMessageWithValue(String topic, String payload) {
        publishMessage(topic, payload);
    }

    @When("I send a message on topic {string} with payload:")
    public void serviceReceivesMqttMessageWithBody(String topic, String payload) {
        publishMessage(topic, payload);
    }

    @Then("the service publishes a message on topic {string} with payload:")
    public void servicePublishesMqttMessage(String topic, String expected) {
        try {
            await().atMost(5, SECONDS).until(
                    () -> receivedMessages.get(topic) != null &&
                            receivedMessages.get(topic).stream()
                                    .anyMatch(mqttMessage -> jsonStringMatches(
                                            new String(mqttMessage.getPayload()),
                                            expected,
                                            Option.IGNORING_ARRAY_ORDER
                                    ))
            );
        } catch (ConditionTimeoutException e) {
            // If no message was found that matches the expected, we still want to see
            // the differences between the actual end expected message.

            if (receivedMessages.get(topic) == null) {
                throw new AssertionError("No message was received on topic " + topic);
            }

            assertThatJson(new String(receivedMessages.get(topic).getLast().getPayload()))
                    .when(Option.IGNORING_ARRAY_ORDER)
                    .isEqualTo(expected);
        }
    }

    @And("a request on {string} returns status {int} with body:")
    public void requestUrl(String url, int statusCode, String expectedBody) {
        response = given()
                .auth()
                .basic(username, "secret")
                .port(port)
                .get(url);
        assertEquals(statusCode, response.getStatusCode());
        assertThatJson(response.getBody().prettyPrint())
                .when(Option.IGNORING_ARRAY_ORDER, Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expectedBody);
    }

    @When("the car is charging at home")
    public void theCarIsChargingAtHome() {
        publishMessage("teslamate/cars/1/latitude", "51.1");
        publishMessage("teslamate/cars/1/longitude", "5.2");
        publishMessage("teslamate/cars/1/charger_power", "0");
        publishMessage("teslamate/cars/1/state", "charging");
        publishMessage("teslamate/cars/1/charger_power", "5");
    }

    @Then("I let the charge session run for {long} seconds")
    public void waitFor(long seconds) throws InterruptedException {
        SECONDS.sleep(seconds);
    }

    private static void publishMessage(String topic, String payload) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());

        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            System.out.println("Failed to publish message.");
        }
    }

    private static boolean jsonStringMatches(String actual, String expected, Option option, Option... options) {
        if (actual == null) {
            return false;
        }

        try {
            assertThatJson(actual)
                    .when(option, options)
                    .isEqualTo(expected);

            return true;
        } catch (AssertionError e) {
            return false;
        }
    }
}
