package nl.dimensiontech.domotics.chargerservice.integration;

import nl.dimensiontech.domotics.chargerservice.ChargerServiceTestConfiguration;
import nl.dimensiontech.domotics.chargerservice.domain.Car;
import nl.dimensiontech.domotics.chargerservice.domain.CarState;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import nl.dimensiontech.domotics.chargerservice.message.handler.CarMessageHandler;
import nl.dimensiontech.domotics.chargerservice.message.handler.ChargerMessageHandler;
import nl.dimensiontech.domotics.chargerservice.message.handler.OutboundMessageHandler;
import nl.dimensiontech.domotics.chargerservice.repository.CarRepository;
import nl.dimensiontech.domotics.chargerservice.repository.ChargeSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static nl.dimensiontech.domotics.chargerservice.util.MessageUtil.createMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = ChargerServiceTestConfiguration.class)
public class ChargeSessionIntegrationTest {

    @MockBean
    protected JavaMailSender javaMailSender;

    @MockBean
    protected OutboundMessageHandler outboundMessageHandler;

    @Autowired
    private ChargeSessionRepository sessionRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ChargerMessageHandler chargerMessageHandler;

    @Autowired
    private CarMessageHandler carMessageHandler;

    @BeforeEach
    public void test() {
        sessionRepository.deleteAll();
        carRepository.deleteAll();
    }

    @Test
    public void shouldCreateNewAnonymousChargeSession() {
        // given & when
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Import", "123456"));
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Power", "4.467"));

        // then
        List<ChargeSession> chargeSessions = toList(sessionRepository.findAll());
        assertThat(chargeSessions).hasSize(1);
        ChargeSession chargeSession = chargeSessions.get(0);
        assertThat(chargeSession.getChargeSessionType()).isEqualTo(ChargeSessionType.ANONYMOUS);
        assertThat(chargeSession.getStartkWh()).isEqualTo(123456f);

        verify(outboundMessageHandler, times(1)).sendMessage(anyString());
    }

    @Test
    public void shouldEndAnonymousChargeSession() throws Exception {
        // given & when
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Import", "5000"));
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Power", "4.467"));

        TimeUnit.SECONDS.sleep(5);

        chargerMessageHandler.handleMessage(createMessage("foo/bar/Import", "5044"));
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Power", "0.000"));

        // then
        List<ChargeSession> chargeSessions = toList(sessionRepository.findAll());
        assertThat(chargeSessions).hasSize(1);

        ChargeSession chargeSession = chargeSessions.get(0);
        assertThat(chargeSession.getChargeSessionType()).isEqualTo(ChargeSessionType.ANONYMOUS);
        assertThat(chargeSession.getStartkWh()).isEqualTo(5000f);
        assertThat(chargeSession.getEndkWh()).isEqualTo(5044f);
        assertThat(Duration.between(chargeSession.getStartedAt(), chargeSession.getEndedAt()).toSeconds()).isEqualTo(5);

        verify(outboundMessageHandler, times(2)).sendMessage(anyString());
    }

    @Test
    public void shouldCreateNewRegisteredChargeSession() {

        // given
        createCar("TestCar", CarState.ASLEEP, "51.1", "5.2", 1234);

        // when
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Import", "123456"));
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Power", "4.467"));
        carMessageHandler.handleMessage(createMessage("foo/car/1/state", CarState.CHARGING.carstate));

        // then
        List<ChargeSession> chargeSessions = toList(sessionRepository.findAll());
        assertThat(chargeSessions).hasSize(1);
        ChargeSession chargeSession = chargeSessions.get(0);
        assertThat(chargeSession.getChargeSessionType()).isEqualTo(ChargeSessionType.REGISTERED);
        assertThat(chargeSession.getStartkWh()).isEqualTo(123456f);
        assertThat(chargeSession.getCar()).isNotNull();

        Car car = chargeSession.getCar();
        assertThat(car.getId()).isEqualTo(1L);

        verify(outboundMessageHandler, times(2)).sendMessage(anyString());
    }

    @Test
    public void shouldCreateNewRegisteredChargeSessionBasedOnChargerPower() {
        // given
        createCar("TestCar", CarState.SUSPENDED, "51.1", "5.2", 1234);

        // when
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Import", "123456"));
        chargerMessageHandler.handleMessage(createMessage("foo/bar/Power", "4.467"));
        carMessageHandler.handleMessage(createMessage("foo/car/1/charger_power", "3"));

        // then
        List<ChargeSession> chargeSessions = toList(sessionRepository.findAll());
        assertThat(chargeSessions).hasSize(1);
        ChargeSession chargeSession = chargeSessions.get(0);
        assertThat(chargeSession.getChargeSessionType()).isEqualTo(ChargeSessionType.REGISTERED);
        assertThat(chargeSession.getStartkWh()).isEqualTo(123456f);

        verify(outboundMessageHandler, times(2)).sendMessage(anyString());
    }

    private void createCar(String name, CarState carState, String latitude, String longitude, int odoMeter) {
        carMessageHandler.handleMessage(createMessage("foo/car/1/display_name", name));
        carMessageHandler.handleMessage(createMessage("foo/car/1/state", carState.carstate));
        carMessageHandler.handleMessage(createMessage("foo/car/1/latitude", latitude));
        carMessageHandler.handleMessage(createMessage("foo/car/1/longitude", longitude));
        carMessageHandler.handleMessage(createMessage("foo/car/1/odometer", Integer.toString(odoMeter)));
    }

    private List<ChargeSession> toList(Iterable<ChargeSession> chargeSessions) {
        List<ChargeSession> list = new ArrayList<>();
        chargeSessions.forEach((list::add));
        return list;
    }
}
