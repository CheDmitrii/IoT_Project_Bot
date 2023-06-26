package com.dmitrii.lockbot.Controllers.Mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.stereotype.Component;

import static com.dmitrii.lockbot.Elements.MqttClients.MqttClientFactory;
import static com.dmitrii.lockbot.Elements.MqttClients.connectOptions;


public class MqttPublisher {


    @Autowired
    @Qualifier("client")
    private MqttClient mqttClient;



    public void publish(final String topic, final String payload, int qos, boolean retained)
            throws MqttPersistenceException, MqttException {

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(retained);

        if (mqttClient.isConnected()){
//            System.out.println("conected");
            mqttClient.publish(topic, mqttMessage);
        }else {
            System.out.println("not conected to send message");
        }
//        System.out.println("sended message");

//        mqttClient.disconnect();
    }
}
