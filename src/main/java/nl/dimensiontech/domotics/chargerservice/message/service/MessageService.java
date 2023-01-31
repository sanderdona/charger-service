package nl.dimensiontech.domotics.chargerservice.message.service;

public interface MessageService<T> {

    void sendMessage(T t);

    void sendMessage(T t, boolean retain);

    void sendMessage(T t, String topic);

    void sendMessage(T t, String topic, boolean retain);
}
