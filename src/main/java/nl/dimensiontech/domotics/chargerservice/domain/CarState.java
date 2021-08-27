package nl.dimensiontech.domotics.chargerservice.domain;

public enum CarState {
    ASLEEP("asleep"),
    ONLINE("online"),
    CHARGING("charging"),
    SUSPENDED("suspended"),
    UNAVAILABLE("unavailable");

    public final String carstate;

    CarState(String state) {
        this.carstate = state;
    }



}
