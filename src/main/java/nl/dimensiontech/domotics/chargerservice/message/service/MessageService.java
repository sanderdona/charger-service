package nl.dimensiontech.domotics.chargerservice.message.service;

public interface MessageService<T> {

    void sendMessage(T t);
}
