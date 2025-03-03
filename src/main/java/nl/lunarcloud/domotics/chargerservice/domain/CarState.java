package nl.lunarcloud.domotics.chargerservice.domain;

public enum CarState {
    ASLEEP("asleep"),
    ONLINE("online"),
    OFFLINE("offline"),
    CHARGING("charging"),
    DRIVING("driving"),
    UPDATING("updating"),
    SUSPENDED("suspended"),
    UNAVAILABLE("unavailable");

    public final String carstate;

    CarState(String state) {
        this.carstate = state;
    }

}
