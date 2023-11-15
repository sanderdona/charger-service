package nl.dimensiontech.domotics.chargerservice.message.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(Include.NON_NULL)
public class ChargeSessionMessage {

    private UUID id;
    private VehicleMessage vehicle;
    private Integer odoMeter;
    private String type;
    private LocalDateTime startedAt;
    private Double startKwh;
    private LocalDateTime endedAt;
    private Double endKwh;
    private Double totalKwh;
}
