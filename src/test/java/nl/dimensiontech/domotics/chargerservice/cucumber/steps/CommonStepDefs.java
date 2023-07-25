package nl.dimensiontech.domotics.chargerservice.cucumber.steps;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import net.javacrumbs.jsonunit.core.Option;
import nl.dimensiontech.domotics.chargerservice.config.ConfigProperties;
import nl.dimensiontech.domotics.chargerservice.cucumber.CallbackHandler;
import nl.dimensiontech.domotics.chargerservice.repository.ChargeSessionRepository;
import org.awaitility.Duration;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonStepDefs extends CallbackHandler {

    @LocalServerPort
    private int port;

    private final ChargeSessionRepository chargeSessionRepository;

    private Response response;

    @Autowired
    public CommonStepDefs(ConfigProperties properties, ChargeSessionRepository chargeSessionRepository) {
        super(properties);
        this.chargeSessionRepository = chargeSessionRepository;
    }

    @After
    public void cleanup() {
        receivedMessages.clear();
        chargeSessionRepository.deleteAll();
    }

    @Given("a car with the name {string}")
    public void aCarWithName(String name) {
        publishMessage("teslamate/cars/1/display_name", name);
    }

    @When("I send a message on topic {string} with payload {string}")
    public void serviceReceivesMqttMessage(String topic, String payload) {
        publishMessage(topic, payload);
    }

    @Then("the service publishes a message on topic {string} with payload:")
    public void servicePublishesMqttMessage(String topic, String expected) {
        await().atMost(Duration.FIVE_SECONDS).until(() -> receivedMessages.get(topic), is(notNullValue()));

        MqttMessage lastMessage = getLastMessage(topic);

        assertThatJson(new String(lastMessage.getPayload()))
                .when(Option.IGNORING_ARRAY_ORDER, Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expected);
    }

    @And("a request on {string} returns status {int} with body:")
    public void requestUrl(String url, int statusCode, String expectedBody) {
        response = given().port(port).get(url);
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

    private static void publishMessage(String topic, String payload) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());

        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            System.out.println("Failed to publish message.");
        }
    }
}
