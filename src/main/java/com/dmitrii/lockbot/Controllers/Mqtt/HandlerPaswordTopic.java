package com.dmitrii.lockbot.Controllers.Mqtt;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class HandlerPaswordTopic implements MessageHandler {
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
        if (topic.equals("IoTLock/password")) {
            System.out.println(topic);
            System.out.println(message.getPayload());
        }
    }
}
