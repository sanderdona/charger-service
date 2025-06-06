package nl.lunarcloud.domotics.chargerservice.cucumber.steps;

import io.cucumber.java.en.Given;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSession;
import nl.lunarcloud.domotics.chargerservice.domain.ChargeSessionType;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static nl.lunarcloud.domotics.chargerservice.util.Formatter.dateTimeFormatter;

public class ChargeSessionSteps {

    @Autowired
    private CommonStepDefs commonStepDefs;

    @Given("a(n) {} charge session started at {string} on {double} and ended at {string} on {double}")
    public void chargeSessionStarted(String type, String startedAt, Double startKwh, String endedAt, Double endKwh) {
        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setChargeSessionType(ChargeSessionType.valueOf(type.toUpperCase()));
        chargeSession.setStartKwh(startKwh);
        chargeSession.setEndKwh(endKwh);
        chargeSession.setTotalKwh(BigDecimal.valueOf(endKwh).subtract(BigDecimal.valueOf(startKwh)).doubleValue());
        chargeSession.setStartedAt(LocalDateTime.parse(startedAt, dateTimeFormatter()));
        chargeSession.setEndedAt(LocalDateTime.parse(endedAt, dateTimeFormatter()));
        commonStepDefs.chargeSessionRepository.save(chargeSession);
    }

    @Given("a registered charge session started at {string} on {double} and ended at {string} on {double} with odo-meter {int}")
    public void registeredChargeSessionStarted(String startedAt, Double startKwh, String endedAt, Double endKwh, int odoMeter) {
        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setChargeSessionType(ChargeSessionType.REGISTERED);
        chargeSession.setOdoMeter(odoMeter);
        chargeSession.setStartKwh(startKwh);
        chargeSession.setEndKwh(endKwh);
        chargeSession.setTotalKwh(BigDecimal.valueOf(endKwh).subtract(BigDecimal.valueOf(startKwh)).doubleValue());
        chargeSession.setStartedAt(LocalDateTime.parse(startedAt, dateTimeFormatter()));
        chargeSession.setEndedAt(LocalDateTime.parse(endedAt, dateTimeFormatter()));
        commonStepDefs.chargeSessionRepository.save(chargeSession);
    }

}
