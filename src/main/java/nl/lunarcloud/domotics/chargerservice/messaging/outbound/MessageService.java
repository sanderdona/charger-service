package nl.lunarcloud.domotics.chargerservice.messaging.outbound;

public interface MessageService<T> {

    void sendMessage(T t);

    void sendMessage(T t, boolean retain);

    void sendMessage(T t, String topic);

    void sendMessage(T t, String topic, boolean retain);
}
