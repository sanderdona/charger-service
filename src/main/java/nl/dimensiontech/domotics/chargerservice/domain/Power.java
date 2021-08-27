package nl.dimensiontech.domotics.chargerservice.domain;

import lombok.Data;

@Data
public class Power {

    private float totalPower;
    private float l1Power;
    private float l2Power;
    private float l3Power;

    public static Power toPower(String string) {
        Power power = new Power();
        power.setTotalPower(Float.parseFloat(string));
        return power;
    }

}
