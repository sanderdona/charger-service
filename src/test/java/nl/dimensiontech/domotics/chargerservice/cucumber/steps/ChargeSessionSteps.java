package nl.dimensiontech.domotics.chargerservice.cucumber.steps;

import io.cucumber.java.en.Given;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSession;
import nl.dimensiontech.domotics.chargerservice.domain.ChargeSessionType;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static nl.dimensiontech.domotics.chargerservice.util.Formatter.dateTimeFormatter;

public class ChargeSessionSteps {

    @Autowired
    private CommonStepDefs commonStepDefs;

    @Given("a(n) {} charge session started at {string} on {double} and ended at {string} on {double}")
    public void bla(String type, String startedAt, Double startKwh, String endedAt, Double endKwh) {
        ChargeSession chargeSession = new ChargeSession();
        chargeSession.setChargeSessionType(ChargeSessionType.valueOf(type.toUpperCase()));
        chargeSession.setStartkWh(startKwh);
        chargeSession.setEndkWh(endKwh);
        chargeSession.setTotalkwH(endKwh - startKwh);
        chargeSession.setStartedAt(LocalDateTime.parse(startedAt, dateTimeFormatter()));
        chargeSession.setEndedAt(LocalDateTime.parse(endedAt, dateTimeFormatter()));
        commonStepDefs.chargeSessionRepository.save(chargeSession);
    }

}
