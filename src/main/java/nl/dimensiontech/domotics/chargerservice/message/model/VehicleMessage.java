package nl.dimensiontech.domotics.chargerservice.message.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleMessage {
    private Long id;
    private String name;
}
