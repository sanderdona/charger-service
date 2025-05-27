package nl.lunarcloud.domotics.chargerservice.messaging.inbound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.lunarcloud.domotics.chargerservice.service.ChargeSessionService;
import nl.lunarcloud.domotics.chargerservice.service.EnergyMeterService;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChargerMessageHandler implements MessageHandler {

    private final ChargeSessionService chargeSessionService;
    private final EnergyMeterService energyMeterService;

    @ServiceActivator(inputChannel = "chargerInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        final var payload = String.valueOf(message.getPayload());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);
            var timestamp = rootNode.path("timestamp").longValue();
            var importedEnergy = rootNode.path("values").path("total_kwh_import").doubleValue();
            var chargePower = rootNode.path("values").path("power_total").floatValue();

            log.debug("Received imported energy message {} kWh", importedEnergy);
            energyMeterService.setCurrentReading(importedEnergy, timestamp);

            log.debug("Received power message {} kW", chargePower);
            chargeSessionService.handleChargePowerUpdate(chargePower);

        } catch (Exception e) {
            log.error("Failed handling received message", e);
        }
    }
}
