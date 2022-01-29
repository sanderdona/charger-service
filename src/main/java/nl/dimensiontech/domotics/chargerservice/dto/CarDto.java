package nl.dimensiontech.domotics.chargerservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarDto {
    private Long id;
    private String name;
}
