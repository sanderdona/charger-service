package nl.dimensiontech.domotics.chargerservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(Include.NON_NULL)
public class ChargeSessionDto {

    private Long id;
    private CarDto car;
    private Integer odoMeter;
    private String type;
    private LocalDateTime startedAt;
    private Double startkWh;
    private LocalDateTime endedAt;
    private Double endkWh;
    private Double totalkwH;
}
