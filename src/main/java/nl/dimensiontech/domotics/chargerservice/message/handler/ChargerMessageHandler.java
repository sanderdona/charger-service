package nl.dimensiontech.domotics.chargerservice.message.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dimensiontech.domotics.chargerservice.service.ChargeSessionService;
import nl.dimensiontech.domotics.chargerservice.service.EnergyMeterService;
import nl.dimensiontech.domotics.chargerservice.util.TopicNameUtil;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChargerMessageHandler implements MessageHandler {

    private static final String POWER_TOPIC = "Power";
    private static final String IMPORT_TOPIC = "Import";

    private final ChargeSessionService chargeSessionService;
    private final EnergyMeterService energyMeterService;

    @ServiceActivator(inputChannel = "chargerInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {

        String topicName = TopicNameUtil.getLastTopicName(message);
        String payload = String.valueOf(message.getPayload());

        if (POWER_TOPIC.equals(topicName)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(payload);

                float chargePower = rootNode.path("values").path("power_total").floatValue();
                log.debug("Received power message {} kW", chargePower);
                chargeSessionService.handleChargePowerUpdate(chargePower);

            } catch (JsonProcessingException e) {
                log.error("Could not parse laadpaal payload");
            }
        }

        if (IMPORT_TOPIC.equals(topicName)) {
            double importedEnergy = Double.parseDouble(payload);
            log.debug("Received imported energy message {} kWh", importedEnergy);
            energyMeterService.setCurrentReading(importedEnergy);
        }
    }
}
