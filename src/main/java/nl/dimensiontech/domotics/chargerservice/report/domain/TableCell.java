package nl.dimensiontech.domotics.chargerservice.report.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
public class TableCell {
    private String value;
    private Color fillColor;

    public TableCell(String value) {
        this.value = value;
    }
}
